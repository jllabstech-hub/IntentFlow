package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * On-device local user behavior history for personalized ranking.
 * Fully immutable and serializable via Kotlin Serialization.
 */
@Serializable
data class UserBehaviorHistory(
    val intentUsageCounts: Map<String, Int> = emptyMap(),
    val recentIntents: List<String> = emptyList(),
    val slotValueFrequency: Map<String, Map<String, Int>> = emptyMap(),
    val lastUsedTimestamp: Map<String, Long> = emptyMap()
)
