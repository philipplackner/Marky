rootProject.name = "Marky"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":androidApp")
include(":composeApp")
include(":core:domain")
include(":core:database")
include(":core:data")
include(":core:files")
include(":core:markdown")
include(":core:presentation")
include(":core:designsystem")
include(":feature:notes:domain")
include(":feature:notes:data")
include(":feature:notes:presentation")
include(":feature:search:domain")
include(":feature:search:data")
include(":feature:search:presentation")
