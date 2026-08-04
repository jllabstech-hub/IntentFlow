package com.intentflow.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class RegressionType {
    INTENT_CHANGE,
    SLOT_EXTRACTION_CHANGE,
    RANKING_CHANGE,
    EXECUTION_CHANGE,
    LATENCY_REGRESSION,
    MEMORY_REGRESSION
}

@Serializable
data class DetectedRegression(
    val regressionId: String,
    val type: RegressionType,
    val utterance: String,
    val previousValue: String,
    val newValue: String,
    val deltaMsOrMb: Float = 0f,
    val isCritical: Boolean = true
)

@Serializable
data class RegressionSuite(
    val suiteId: String,
    val name: String,
    val baselineCatalogVersionCode: Int,
    val targetCatalogVersionCode: Int,
    val testUtterances: List<String>,
    val maxAllowedLatencyIncreasePercent: Float = 10.0f,
    val maxAllowedMemoryIncreaseMb: Float = 5.0f
)

@Serializable
data class RegressionReport(
    val reportId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val suiteId: String,
    val totalUtterancesTested: Int,
    val isPassed: Boolean,
    val totalRegressionsFound: Int,
    val criticalRegressionsCount: Int,
    val regressions: List<DetectedRegression> = emptyList()
)
