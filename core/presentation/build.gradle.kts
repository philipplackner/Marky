plugins {
    alias(libs.plugins.convention.cmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.core.markdown)
            implementation(projects.core.designsystem)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
    }
}
