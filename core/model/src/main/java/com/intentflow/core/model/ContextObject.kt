package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Immutable ContextObject capturing on-device environmental signals.
 * Fully serializable via Kotlin Serialization.
 */
@Serializable
data class ContextObject(
    val timestamp: Long = System.currentTimeMillis(),
    val currentTimeString: String = "",
    val currentDateString: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationName: String? = null,
    val recentContacts: List<String> = emptyList(),
    val installedApps: List<String> = emptyList(),
    val clipboardText: String? = null,
    val recentIntentIds: List<String> = emptyList(),
    val userPreferences: Map<String, String> = emptyMap()
) {
    /**
     * Validates ContextObject field bounds.
     */
    fun validate(): ValidationResult {
        if (latitude != null && latitude !in -90.0..90.0) {
            return ValidationResult.Invalid("latitude must be between -90.0 and 90.0")
        }
        if (longitude != null && longitude !in -180.0..180.0) {
            return ValidationResult.Invalid("longitude must be between -180.0 and 180.0")
        }
        if (timestamp <= 0) {
            return ValidationResult.Invalid("timestamp must be positive")
        }
        return ValidationResult.Valid
    }
}
