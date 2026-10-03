import com.android.build.api.dsl.ApplicationExtension
import com.plcoding.marky.convention.libs
import com.plcoding.marky.convention.versionInt
import com.plcoding.marky.convention.versionString
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
            }

            extensions.configure<ApplicationExtension> {
                namespace = libs.versionString("projectApplicationId")
                compileSdk = libs.versionInt("projectCompileSdkVersion")

                defaultConfig {
                    applicationId = libs.versionString("projectApplicationId")
                    minSdk = libs.versionInt("projectMinSdkVersion")
                    targetSdk = libs.versionInt("projectTargetSdkVersion")
                    versionCode = libs.versionInt("projectVersionCode")
                    versionName = libs.versionString("projectVersionName")
                }
                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }
                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = false
                    }
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
            }

            extensions.configure<KotlinAndroidProjectExtension> {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
        }
    }
}
