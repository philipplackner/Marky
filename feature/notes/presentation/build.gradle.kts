plugins {
    alias(libs.plugins.convention.cmp.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.notes.domain)
            implementation(projects.core.markdown)
            implementation(projects.core.files)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}

compose.resources {
    packageOfResClass = "com.plcoding.marky.feature.notes.presentation.generated.resources"
}
