package com.plcoding.marky.convention

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureKotlinMultiplatform() {
    extensions.configure<KotlinMultiplatformExtension> {
        targets.withType<KotlinMultiplatformAndroidLibraryTarget>().configureEach {
            compileSdk = libs.versionInt("projectCompileSdkVersion")
            minSdk = libs.versionInt("projectMinSdkVersion")
            namespace = pathToPackageName()

            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }
            androidResources {
                enable = true
            }
            withHostTest {
                isIncludeAndroidResources = true
            }
        }

        iosArm64()
        iosSimulatorArm64()

        applyDefaultHierarchyTemplate()

        compilerOptions {
            freeCompilerArgs.add("-Xexpect-actual-classes")
            optIn.add("kotlin.time.ExperimentalTime")
        }
    }
}
