package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * On-device context snapshot for slot enrichment.
 */
@Serializable
data class ContextSnapshot(
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
    fun toContextObject(): ContextObject = ContextObject(
        timestamp = timestamp,
        currentTimeString = currentTimeString,
        currentDateString = currentDateString,
        latitude = latitude,
        longitude = longitude,
        locationName = locationName,
        recentContacts = recentContacts,
        installedApps = installedApps,
        clipboardText = clipboardText,
        recentIntentIds = recentIntentIds,
        userPreferences = userPreferences
    )
}
