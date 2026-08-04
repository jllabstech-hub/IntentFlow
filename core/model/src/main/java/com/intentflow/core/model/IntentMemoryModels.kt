package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Category classification for stored intent memories.
 */
@Serializable
enum class MemoryCategory {
    SLOT_PREFERENCE,      // Specific slot preference (e.g. "home_airport" -> "SFO")
    DOMAIN_PREFERENCE,    // Domain-wide preference (e.g. "travel" -> "Seat: Aisle, Meal: Vegan")
    CONTACT_PREFERENCE,   // Preferred contact binding (e.g. "favorite_doctor" -> "Dr. Smith")
    PAYMENT_PREFERENCE    // Preferred payment method binding (e.g. "default_upi" -> "Google Pay")
}

/**
 * Individual encrypted memory entry stored locally on-device.
 */
@Serializable
data class IntentMemoryEntry(
    val memoryId: String,
    val domain: String,
    val slotName: String,
    val preferredValue: String,
    val displayLabel: String,
    val category: MemoryCategory = MemoryCategory.SLOT_PREFERENCE,
    val confidenceScore: Float = 1.0f,
    val isUserConfirmed: Boolean = true,
    val lastUsedTimestamp: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap()
)

/**
 * Domain-level memory snapshot grouping preferences.
 */
@Serializable
data class DomainPreference(
    val domain: String,
    val preferences: Map<String, IntentMemoryEntry> = emptyMap()
)
