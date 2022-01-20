package org.jesperancinha.plugins.omni.reporter

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by jofisaes on 20/01/2022
 */
class OmniReporterPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.task("omni") {
            println("This is where the gradle plugin will start")
        }
    }
}
