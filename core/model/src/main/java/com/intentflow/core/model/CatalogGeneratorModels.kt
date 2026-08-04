package com.intentflow.core.model

import kotlinx.serialization.Serializable

@Serializable
data class CatalogDiffReport(
    val versionA: Int,
    val versionB: Int,
    val addedIntents: List<String> = emptyList(),
    val removedIntents: List<String> = emptyList(),
    val modifiedSlots: List<String> = emptyList(),
    val addedUtterancesCount: Int = 0,
    val isBreakingChange: Boolean = false,
    val breakingChangeDetails: List<String> = emptyList()
)

@Serializable
data class CatalogGeneratorStats(
    val totalDomains: Int,
    val totalIntents: Int,
    val totalSlots: Int,
    val totalUtterances: Int,
    val totalEntities: Int,
    val totalCapabilities: Int,
    val totalSchemas: Int,
    val deduplicatedUtterancesCount: Int,
    val generationTimeMs: Long
)

@Serializable
data class CatalogGenerationOptions(
    val targetVersionCode: Int,
    val targetVersionName: String,
    val deduplicateUtterances: Boolean = true,
    val normalizeSlots: Boolean = true,
    val enforceStrictValidation: Boolean = true,
    val generateDiffReport: Boolean = true
)
