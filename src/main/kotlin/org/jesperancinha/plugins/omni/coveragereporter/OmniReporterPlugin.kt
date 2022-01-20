package org.jesperancinha.plugins.omni.coveragereporter

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jesperancinha.plugins.omni.reporter.IncompleteCodacyApiTokenConfigurationException
import org.jesperancinha.plugins.omni.reporter.OmniBuild
import org.jesperancinha.plugins.omni.reporter.OmniProject
import org.jesperancinha.plugins.omni.reporter.domain.CodacyApiTokenConfig
import org.jesperancinha.plugins.omni.reporter.pipelines.PipelineImpl
import org.jesperancinha.plugins.omni.reporter.processors.CodacyProcessor
import org.jesperancinha.plugins.omni.reporter.processors.CodecovProcessor
import org.jesperancinha.plugins.omni.reporter.processors.CoverallsReportsProcessor
import org.slf4j.LoggerFactory
import java.io.File

private class GradleOmniBuild(
    override val testOutputDirectory: String,
    override val directory: String
) : OmniBuild

private class GradleOmniProject(
    override val compileSourceRoots: List<String>?,
    override val build: OmniBuild?
) : OmniProject

private val List<Project>.toOmniProjects: List<OmniProject?>
    get() = map {
        GradleOmniProject(
            listOf(it.path),
            GradleOmniBuild(it.buildDir.absolutePath, it.buildDir.absolutePath)
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
    var coverallsToken: String? = null
    var codecovToken: String? = null
    var codacyToken: String? = null
    var codacyApiToken: String? = null
    var codacyOrganizationProvider: String? = null
    var codacyUserName: String? = null
    var codacyProjectName: String? = null
    val extraSourceFolders: List<File> = emptyList()
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
                    extension.projectBaseDir,
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
                    extension.coverallsToken,
                    extension.codecovToken,
                    extension.codacyToken,
                    extension.codacyApiToken,
                    extension.codacyOrganizationProvider,
                    extension.codacyUserName,
                    extension.codacyProjectName,
                    extension.extraSourceFolders,
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
        coverallsToken: String?,
        codecovToken: String?,
        codacyToken: String?,
        codacyApiToken: String?,
        codacyOrganizationProvider: String?,
        codacyUserName: String?,
        codacyProjectName: String?,
        extraSourceFolders: List<File>,
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
        val effectiveCodacyUserName = codacyUserName ?: environment["CODACY_USERNAME"]
        val effectiveCodacyProjectName = codacyProjectName ?: environment["CODACY_PROJECT_NAME"]
        val effectiveCodecovToken = codecovToken ?: environment["CODECOV_TOKEN"]

        val allProjects = project.findAllSearchFolders.distinct()

        logLine()
        logger.info("Coveralls URL: $coverallsUrl")
        logger.info("Codacy URL: $codacyUrl")
        logger.info("Codecov URL: $codecovUrl")
        logger.info("Coveralls token: ${checkToken(coverallsToken)}")
        logger.info("Codecov token: ${checkToken(codecovToken)}")
        logger.info("Codacy token: ${checkToken(codacyToken)}")
        logger.info("Codacy API token: ${checkToken(codacyApiToken)}")
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
        logger.info("ignoreTestBuildDirectory: $ignoreTestBuildDirectory")
        logger.info("branchCoverage: $branchCoverage")
        logger.info("useCoverallsCount: $useCoverallsCount")
        logger.info("extraSourceFolders: ${extraSourceFolders.joinToString(";")}")
        logLine()

        val currentPipeline = PipelineImpl.currentPipeline

        effectiveCoverallsToken?.let { token ->
            if (!disableCoveralls)
                CoverallsReportsProcessor(
                    coverallsToken = token,
                    coverallsUrl = coverallsUrl,
                    currentPipeline = currentPipeline,
                    allProjects = allProjects.toOmniProjects,
                    projectBaseDir = projectBaseDir,
                    failOnUnknown = failOnUnknown,
                    failOnReportNotFound = failOnReportNotFound,
                    failOnReportSending = failOnReportSendingError,
                    failOnXmlParseError = failOnXmlParsingError,
                    branchCoverage = branchCoverage,
                    ignoreTestBuildDirectory = ignoreTestBuildDirectory,
                    useCoverallsCount = useCoverallsCount
                ).processReports()
        }

        if ((isCodacyAPIConfigured || effectiveCodacyToken != null) && !disableCodacy) {
            val codacyApiTokenConfig = effectiveCodacyToken?.let {
                CodacyApiTokenConfig(
                    codacyApiToken = effectiveCodacyApiToken ?: throw IncompleteCodacyApiTokenConfigurationException(),
                    codacyOrganizationProvider = effectiveCodacyOrganizationProvider
                        ?: throw IncompleteCodacyApiTokenConfigurationException(),
                    codacyUsername = effectiveCodacyUserName ?: throw IncompleteCodacyApiTokenConfigurationException(),
                    codacyProjectName = effectiveCodacyProjectName
                        ?: throw IncompleteCodacyApiTokenConfigurationException()
                )
            }
            CodacyProcessor(
                token = codacyToken,
                apiToken = codacyApiTokenConfig,
                codacyUrl = codacyUrl,
                currentPipeline = currentPipeline,
                allProjects = allProjects.toOmniProjects,
                projectBaseDir = projectBaseDir,
                failOnReportNotFound = failOnReportNotFound,
                failOnReportSending = failOnReportSendingError,
                failOnXmlParseError = failOnXmlParsingError,
                failOnUnknown = failOnUnknown,
                ignoreTestBuildDirectory = ignoreTestBuildDirectory,
            ).processReports()
        }


        effectiveCodecovToken?.let { token ->
            if (!disableCodecov)
                CodecovProcessor(
                    token = token,
                    codecovUrl = codecovUrl,
                    currentPipeline = currentPipeline,
                    allProjects = allProjects.toOmniProjects,
                    projectBaseDir = projectBaseDir,
                    failOnReportNotFound = failOnReportNotFound,
                    failOnReportSending = failOnReportSendingError,
                    failOnUnknown = failOnUnknown,
                    ignoreTestBuildDirectory = ignoreTestBuildDirectory,
                ).processReports()
        }
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

