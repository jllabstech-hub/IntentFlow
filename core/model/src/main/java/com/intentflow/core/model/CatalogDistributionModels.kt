package com.intentflow.core.model

import kotlinx.serialization.Serializable

@Serializable
data class CatalogPackageManifest(
    val versionCode: Int,
    val versionName: String,
    val minAppVersionCode: Int = 1,
    val releaseTimestamp: Long = System.currentTimeMillis(),
    val sha256Checksum: String,
    val downloadUrl: String? = null,
    val isInstalled: Boolean = false,
    val isActive: Boolean = false,
    val localZipPath: String? = null,
    val entityCounts: Map<String, Int> = emptyMap()
)

@Serializable
data class CatalogValidationReport(
    val isValid: Boolean,
    val versionCode: Int,
    val errors: List<String> = emptyList(),
    val warnings: List<String> = emptyList()
)

@Serializable
enum class CatalogSwitchResult {
    SUCCESS,
    COMPATIBILITY_FAILURE,
    CHECKSUM_MISMATCH,
    VERSION_NOT_FOUND,
    MIGRATION_FAILED
}
