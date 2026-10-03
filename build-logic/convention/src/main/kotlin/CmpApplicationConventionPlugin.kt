import com.plcoding.marky.convention.configureKotlinMultiplatform
import com.plcoding.marky.convention.libs
import com.plcoding.marky.convention.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class CmpApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.kotlin.multiplatform.library")
                apply("org.jetbrains.kotlin.multiplatform")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            configureKotlinMultiplatform()

            extensions.configure<KotlinMultiplatformExtension> {
                targets.withType(KotlinNativeTarget::class.java).configureEach {
                    binaries.framework {
                        baseName = "ComposeApp"
                        isStatic = true
                        binaryOption("bundleId", "com.plcoding.marky.ComposeApp")
                    }
                }
            }

            dependencies {
                "commonMainImplementation"(libs.library("compose-runtime"))
                "commonMainImplementation"(libs.library("compose-foundation"))
                "commonMainImplementation"(libs.library("compose-ui"))
                "commonMainImplementation"(libs.library("compose-components-resources"))
                "commonMainImplementation"(libs.library("compose-uiToolingPreview"))
                "commonMainImplementation"(libs.library("kotlinx-serialization-json"))

                // KMP library modules have no debug variant, so previews need the tooling in androidMain.
                "androidMainImplementation"(libs.library("compose-uiTooling"))

                "commonTestImplementation"(libs.library("kotlin-test"))
            }
        }
    }
}
