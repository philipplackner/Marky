import com.plcoding.marky.convention.libs
import com.plcoding.marky.convention.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class CmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.plcoding.marky.convention.kmp.library")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.compose")
            }

            dependencies {
                "commonMainImplementation"(libs.library("compose-runtime"))
                "commonMainImplementation"(libs.library("compose-foundation"))
                "commonMainImplementation"(libs.library("compose-ui"))
                "commonMainImplementation"(libs.library("compose-components-resources"))
                "commonMainImplementation"(libs.library("compose-uiToolingPreview"))

                // KMP library modules have no debug variant, so previews need the tooling in androidMain.
                "androidMainImplementation"(libs.library("compose-uiTooling"))
            }
        }
    }
}
