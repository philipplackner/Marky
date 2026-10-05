plugins {
    alias(libs.plugins.convention.android.application.compose)
}

dependencies {
    implementation(projects.composeApp)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
}
