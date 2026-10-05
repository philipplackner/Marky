plugins {
    alias(libs.plugins.convention.cmp.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.search.domain)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}

compose.resources {
    packageOfResClass = "com.plcoding.marky.feature.search.presentation.generated.resources"
}
