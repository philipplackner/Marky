plugins {
    alias(libs.plugins.convention.cmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.material3)
        }
    }
}

compose.resources {
    packageOfResClass = "com.plcoding.marky.core.designsystem.generated.resources"
}
