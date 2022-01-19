class OmniReporterPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.task("omni") {
            doLast {
                println("This is where the gradle plugin will start")
            }
        }
    }
}

apply<OmniReporterPlugin>()
