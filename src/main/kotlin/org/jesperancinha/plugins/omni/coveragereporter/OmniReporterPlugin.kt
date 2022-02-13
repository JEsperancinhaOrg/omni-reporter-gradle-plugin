package org.jesperancinha.plugins.omni.coveragereporter

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jesperancinha.plugins.omni.reporter.OmniBuild
import org.jesperancinha.plugins.omni.reporter.OmniProject
import org.jesperancinha.plugins.omni.reporter.OmniReporterCommon
import org.jesperancinha.plugins.omni.reporter.OmniReporterCommon.Companion.HTTPS_API_CODACY_COM
import org.jesperancinha.plugins.omni.reporter.OmniReporterCommon.Companion.HTTPS_CODECOV_IO_UPLOAD
import org.jesperancinha.plugins.omni.reporter.OmniReporterCommon.Companion.HTTPS_COVERALLS_IO_API_V_1_JOBS
import org.jesperancinha.plugins.omni.reporter.domain.api.CodacyApiTokenConfig
import org.jesperancinha.plugins.omni.reporter.parsers.Language
import org.jesperancinha.plugins.omni.reporter.toExtraProjects
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
    var coverallsUrl: String = HTTPS_COVERALLS_IO_API_V_1_JOBS
    var codacyUrl: String = HTTPS_API_CODACY_COM
    var codecovUrl: String = HTTPS_CODECOV_IO_UPLOAD
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
        codacyUsername: String?,
        codacyProjectName: String?,
        extraSourceFolders: List<File>,
        extraReportFolders: List<File>,
        reportRejectList: List<String>,
        project: Project
    ) {

        val allProjects = project.findAllSearchFolders.distinct()
        val extraProjects = extraReportFolders.toExtraProjects(extraSourceFolders)
        val allOmniProjects = allProjects.toOmniProjects + extraProjects
        OmniReporterCommon(
            coverallsUrl,
            codacyUrl,
            codecovUrl,
            sourceEncoding,
            projectBaseDir,
            failOnNoEncoding,
            failOnUnknown,
            failOnReportNotFound,
            failOnReportSendingError,
            failOnXmlParsingError,
            disableCoveralls,
            disableCodacy,
            disableCodecov,
            ignoreTestBuildDirectory,
            useCoverallsCount,
            branchCoverage,
            fetchBranchNameFromEnv,
            coverallsToken,
            codecovToken,
            codacyToken,
            codacyApiToken,
            codacyOrganizationProvider,
            codacyUsername,
            codacyProjectName,
            extraSourceFolders,
            extraReportFolders,
            reportRejectList
        ).execute(allOmniProjects)
    }

}

