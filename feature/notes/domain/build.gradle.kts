plugins {
    alias(libs.plugins.convention.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            api(libs.kotlinx.datetime)
        }
    }
}
