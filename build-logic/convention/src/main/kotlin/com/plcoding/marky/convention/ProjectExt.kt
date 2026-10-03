package com.plcoding.marky.convention

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun VersionCatalog.library(alias: String): Provider<MinimalExternalModuleDependency> {
    return findLibrary(alias).get()
}

internal fun VersionCatalog.versionInt(alias: String): Int {
    return findVersion(alias).get().requiredVersion.toInt()
}

internal fun VersionCatalog.versionString(alias: String): String {
    return findVersion(alias).get().requiredVersion
}
