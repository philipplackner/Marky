import com.android.build.api.dsl.ApplicationExtension
import com.plcoding.marky.convention.libs
import com.plcoding.marky.convention.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.plcoding.marky.convention.android.application")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            extensions.configure<ApplicationExtension> {
                buildFeatures {
                    compose = true
                }
            }

            dependencies {
                "implementation"(libs.library("androidx-activity-compose"))
                "implementation"(libs.library("compose-uiToolingPreview"))
                "debugImplementation"(libs.library("compose-uiTooling"))
            }
        }
    }
}
