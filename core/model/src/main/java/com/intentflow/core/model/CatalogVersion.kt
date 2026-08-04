package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Metadata representing a specific Knowledge Catalog version.
 * Fully immutable and serializable via Kotlin Serialization.
 */
@Serializable
data class CatalogVersion(
    val versionCode: Int,
    val versionName: String,
    val releasedAtTimestamp: Long = System.currentTimeMillis(),
    val supportedMinAppVersion: String = "1.0.0",
    val domainCount: Int = 0,
    val intentCount: Int = 0,
    val utteranceCount: Int = 0,
    val checksum: String = ""
) {
    /**
     * Validates CatalogVersion integrity.
     */
    fun validate(): ValidationResult {
        if (versionCode <= 0) {
            return ValidationResult.Invalid("versionCode must be greater than 0")
        }
        if (versionName.isBlank()) {
            return ValidationResult.Invalid("versionName cannot be blank")
        }
        if (domainCount < 0 || intentCount < 0 || utteranceCount < 0) {
            return ValidationResult.Invalid("Counts cannot be negative")
        }
        return ValidationResult.Valid
    }
}
