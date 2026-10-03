import com.plcoding.marky.convention.configureKotlinMultiplatform
import com.plcoding.marky.convention.libs
import com.plcoding.marky.convention.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.kotlin.multiplatform.library")
                apply("org.jetbrains.kotlin.multiplatform")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            configureKotlinMultiplatform()

            dependencies {
                "commonMainImplementation"(libs.library("kotlinx-serialization-json"))

                "commonTestImplementation"(libs.library("kotlin-test"))
                "commonTestImplementation"(libs.library("assertk"))
                "commonTestImplementation"(libs.library("turbine"))
                "commonTestImplementation"(libs.library("kotlinx-coroutines-test"))
            }
        }
    }
}
