package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Domain entity grouping related intents.
 */
@Serializable
data class Domain(
    val id: String,
    val displayName: String,
    val description: String,
    val iconName: String? = null,
    val intents: List<IntentDefinition> = emptyList()
)
