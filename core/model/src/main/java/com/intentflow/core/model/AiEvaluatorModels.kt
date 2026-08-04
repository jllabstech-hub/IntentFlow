package com.intentflow.core.model

import kotlinx.serialization.Serializable

@Serializable
data class AiProviderMetrics(
    val providerId: String,
    val accuracyPercent: Float,
    val p50LatencyMs: Long,
    val p95LatencyMs: Long,
    val estimatedCostUsd: Double,
    val hallucinationRatePercent: Float,
    val slotCompletionRatePercent: Float,
    val executionSuccessRatePercent: Float
)

@Serializable
data class AiEvaluationSuite(
    val suiteId: String,
    val name: String,
    val providerIds: List<String>,
    val testIntentObjects: List<IntentObject>,
    val maxAllowedHallucinationRate: Float = 2.0f
)

@Serializable
data class AiEvaluationReport(
    val reportId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val suiteId: String,
    val winningProviderId: String,
    val providerMetrics: Map<String, AiProviderMetrics> = emptyMap(),
    val evaluationNotes: String? = null
)
