package org.jesperancinha.plugins.omni.coveragereporter

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jesperancinha.plugins.omni.reporter.*
import org.jesperancinha.plugins.omni.reporter.domain.api.CodacyApiTokenConfig
import org.jesperancinha.plugins.omni.reporter.parsers.Language
import org.jesperancinha.plugins.omni.reporter.processors.CodacyProcessor
import org.jesperancinha.plugins.omni.reporter.processors.CodecovProcessor
import org.jesperancinha.plugins.omni.reporter.processors.CoverallsReportsProcessor
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

class GradleOmniBuild(
    override val testOutputDirectory: String,
    override val directory: String
) : OmniBuild

private class GradleOmniProject(
    override val compileSourceRoots: MutableList<String> = mutableListOf(),
    override val build: OmniBuild?
) : OmniProject

private fun Array<Language>.toSourceDirectories(absolutePath: String) =
    map { File(absolutePath, "src/main/${it.name.lowercase(Locale.getDefault())}").absolutePath }

private val List<Project>.toOmniProjects: List<OmniProject>
    get() = map {
        GradleOmniProject(
            Language.values().toSourceDirectories(it.projectDir.absolutePath).toMutableList(),
            GradleOmniBuild(File(it.buildDir, "test-classes").absolutePath, it.buildDir.absolutePath)
        )
    }

private val OmniReporterPluginExtension.isCodacyAPIConfigured: Boolean
    get() = CodacyApiTokenConfig.isApiTokenConfigure(
        codacyApiToken = codacyApiToken,
        codacyOrganizationProvider = codacyOrganizationProvider,
        codacyUsername = codacyUserName,
        codacyProjectName = codacyProjectName
    )

private val Project?.findAllSearchFolders: List<Project>
    get() = ((this?.allprojects.let {
        it?.addAll(it.flatMap { proj -> proj.childProjects.values.flatMap { subProj -> subProj.findAllSearchFolders } })
        it
    } ?: mutableListOf()) + this).filterNotNull()

open class OmniReporterPluginExtension {
    var coverallsUrl: String = "https://coveralls.io/api/v1/jobs"
    var codacyUrl: String = "https://api.codacy.com"
    var codecovUrl: String = "https://codecov.io/upload"
    var sourceEncoding: String? = "UTF-8"
    var projectBaseDir: File? = null
    var failOnNoEncoding: Boolean = false
    var failOnUnknown: Boolean = false
    var failOnReportNotFound: Boolean = false
    var failOnReportSendingError: Boolean = false
    var failOnXmlParsingError: Boolean = false
    var disableCoveralls: Boolean = false
    var disableCodacy: Boolean = false
    var disableCodecov: Boolean = false
    var ignoreTestBuildDirectory: Boolean = true
    var useCoverallsCount: Boolean = true
    var branchCoverage: Boolean = false
    var fetchBranchNameFromEnv: Boolean = false
    var coverallsToken: String? = null
    var codecovToken: String? = null
    var codacyToken: String? = null
    var codacyApiToken: String? = null
    var codacyOrganizationProvider: String? = null
    var codacyUserName: String? = null
    var codacyProjectName: String? = null
    var extraSourceFolders: List<File> = emptyList()
    var extraReportFolders: List<File> = emptyList()
    var reportRejectList: List<String> = emptyList()
}

/**
 * Created by jofisaes on 20/01/2022
 */
class OmniReporterPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        project.tasks.create("omni") {
            val extension = project.extensions.create("omniConfig", OmniReporterPluginExtension::class.java)
            project.afterEvaluate {
                runReporting(
                    extension.coverallsUrl,
                    extension.codacyUrl,
                    extension.codecovUrl,
                    extension.sourceEncoding,
                    extension.projectBaseDir ?: project.projectDir,
                    extension.failOnNoEncoding,
                    extension.failOnUnknown,
                    extension.failOnReportNotFound,
                    extension.failOnReportSendingError,
                    extension.failOnXmlParsingError,
                    extension.disableCoveralls,
                    extension.disableCodacy,
                    extension.disableCodecov,
                    extension.ignoreTestBuildDirectory,
                    extension.useCoverallsCount,
                    extension.branchCoverage,
                    extension.fetchBranchNameFromEnv,
                    extension.coverallsToken,
                    extension.codecovToken,
                    extension.codacyToken,
                    extension.codacyApiToken,
                    extension.codacyOrganizationProvider,
                    extension.codacyUserName,
                    extension.codacyProjectName,
                    extension.extraSourceFolders,
                    extension.extraReportFolders,
                    extension.reportRejectList,
                    extension.isCodacyAPIConfigured,
                    project
                )
            }
        }
    }

    private fun runReporting(
        coverallsUrl: String,
        codacyUrl: String,
        codecovUrl: String,
        sourceEncoding: String?,
        projectBaseDir: File?,
        failOnNoEncoding: Boolean,
        failOnUnknown: Boolean,
        failOnReportNotFound: Boolean,
        failOnReportSendingError: Boolean,
        failOnXmlParsingError: Boolean,
        disableCoveralls: Boolean,
        disableCodacy: Boolean,
        disableCodecov: Boolean,
        ignoreTestBuildDirectory: Boolean,
        useCoverallsCount: Boolean,
        branchCoverage: Boolean,
        fetchBranchNameFromEnv: Boolean,
        coverallsToken: String?,
        codecovToken: String?,
        codacyToken: String?,
        codacyApiToken: String?,
        codacyOrganizationProvider: String?,
        codacyUserName: String?,
        codacyProjectName: String?,
        extraSourceFolders: List<File>,
        extraReportFolders: List<File>,
        reportRejectList: List<String>,
        isCodacyAPIConfigured: Boolean,
        project: Project
    ) {
        logLine()
        logger.info(javaClass.getResourceAsStream("/banner.txt")?.bufferedReader().use { it?.readText() })
        logLine()


        val environment = System.getenv()
        val effectiveCoverallsToken =
            (coverallsToken ?: environment["COVERALLS_REPO_TOKEN"]) ?: environment["COVERALLS_TOKEN"]
        val effectiveCodacyToken = codacyToken ?: environment["CODACY_PROJECT_TOKEN"]
        val effectiveCodacyApiToken = codacyApiToken ?: environment["CODACY_API_TOKEN"]
        val effectiveCodacyOrganizationProvider =
            codacyOrganizationProvider ?: environment["CODACY_ORGANIZATION_PROVIDER"]
        val effectiveCodacyUsername = codacyUserName ?: environment["CODACY_USERNAME"]
        val effectiveCodacyProjectName = codacyProjectName ?: environment["CODACY_PROJECT_NAME"]
        val effectiveCodecovToken = codecovToken ?: environment["CODECOV_TOKEN"]

        val allProjects = project.findAllSearchFolders.distinct()

        logLine()
        logger.info("Coveralls URL: $coverallsUrl")
        logger.info("Codacy URL: $codacyUrl")
        logger.info("Codecov URL: $codecovUrl")
        logger.info("Coveralls token: ${checkToken(effectiveCoverallsToken)}")
        logger.info("Codecov token: ${checkToken(effectiveCodecovToken)}")
        logger.info("Codacy token: ${checkToken(effectiveCodacyToken)}")
        logger.info("Codacy API token: ${checkToken(effectiveCodacyApiToken)}")
        logger.info("Codacy API fully configured: $isCodacyAPIConfigured")
        logger.info("Source Encoding: $sourceEncoding")
        logger.info("Parent Directory: $projectBaseDir")
        logger.info("failOnNoEncoding: $failOnNoEncoding")
        logger.info("failOnUnknown: $failOnUnknown")
        logger.info("failOnReportNotFound: $failOnReportNotFound")
        logger.info("failOnReportSendingError: $failOnReportSendingError")
        logger.info("failOnXmlParsingError: $failOnXmlParsingError")
        logger.info("disableCoveralls: $disableCoveralls")
        logger.info("disableCodacy: $disableCodacy")
        logger.info("fetchBranchNameFromEnv: $fetchBranchNameFromEnv")
        logger.info("ignoreTestBuildDirectory: $ignoreTestBuildDirectory")
        logger.info("branchCoverage: $branchCoverage")
        logger.info("useCoverallsCount: $useCoverallsCount")
        logger.info("extraSourceFolders: ${extraSourceFolders.joinToString(";")}")
        logger.info("extraReportFolders: ${extraReportFolders.joinToString(";")}")
        logger.info("reportRejectList: ${reportRejectList.joinToString(";")}")
        logLine()

        val extraProjects = extraReportFolders.map {
            OmniProjectGeneric(
                extraSourceFolders.map { src -> src.absolutePath }.toMutableList(),
                OmniBuildGeneric(it.absolutePath, it.absolutePath)
            )
        }
        val allOmniProjects = allProjects.toOmniProjects + extraProjects

        CoverallsReportsProcessor(
            coverallsToken = effectiveCoverallsToken,
            disableCoveralls = disableCoveralls,
            coverallsUrl = coverallsUrl,
            allProjects = allOmniProjects,
            projectBaseDir = projectBaseDir,
            failOnUnknown = failOnUnknown,
            failOnReportNotFound = failOnReportNotFound,
            failOnReportSending = failOnReportSendingError,
            failOnXmlParseError = failOnXmlParsingError,
            fetchBranchNameFromEnv = fetchBranchNameFromEnv,
            branchCoverage = branchCoverage,
            ignoreTestBuildDirectory = ignoreTestBuildDirectory,
            useCoverallsCount = useCoverallsCount,
            reportRejectList = reportRejectList
        ).processReports()

        CodacyProcessor(
            codacyToken = effectiveCodacyToken,
            codacyApiToken = effectiveCodacyApiToken,
            codacyOrganizationProvider = effectiveCodacyOrganizationProvider,
            codacyUsername = effectiveCodacyUsername,
            codacyProjectName = effectiveCodacyProjectName,
            disableCodacy = disableCodacy,
            codacyUrl = codacyUrl,
            allProjects = allOmniProjects,
            projectBaseDir = projectBaseDir,
            failOnReportNotFound = failOnReportNotFound,
            failOnReportSending = failOnReportSendingError,
            failOnXmlParseError = failOnXmlParsingError,
            fetchBranchNameFromEnv = fetchBranchNameFromEnv,
            failOnUnknown = failOnUnknown,
            ignoreTestBuildDirectory = ignoreTestBuildDirectory,
            reportRejectList = reportRejectList
        ).processReports()


        CodecovProcessor(
            codecovToken = effectiveCodecovToken,
            disableCodecov = disableCodecov,
            codecovUrl = codecovUrl,
            allProjects = allOmniProjects,
            projectBaseDir = projectBaseDir ?: throw ProjectDirectoryNotFoundException(),
            failOnReportNotFound = failOnReportNotFound,
            failOnReportSending = failOnReportSendingError,
            failOnUnknown = failOnUnknown,
            fetchBranchNameFromEnv = fetchBranchNameFromEnv,
            ignoreTestBuildDirectory = ignoreTestBuildDirectory,
            reportRejectList = reportRejectList,
        ).processReports()
    }


    private fun logLine() = let {
        logger.info("*".repeat(OMNI_CHARACTER_LINE_NUMBER))
    }

    private fun checkToken(token: String?) = token?.let { "found" } ?: "not found"

    companion object {
        private val logger = LoggerFactory.getLogger(OmniReporterPlugin::class.java)

        const val OMNI_CHARACTER_LINE_NUMBER = 150
    }
}

