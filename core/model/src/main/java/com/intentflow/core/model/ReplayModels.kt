package com.intentflow.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ReplayEnvironmentConfig(
    val targetCatalogVersionCode: Int? = null,
    val targetProviderId: String? = null,
    val targetSearchAlgorithm: String? = null,
    val targetReasoningEnabled: Boolean? = null
)

@Serializable
data class SessionDiffReport(
    val sessionId: String,
    val isOriginalSuccess: Boolean,
    val isReplayedSuccess: Boolean,
    val originalIntentId: String,
    val replayedIntentId: String,
    val originalLatencyMs: Long,
    val replayedLatencyMs: Long,
    val slotDifferences: Map<String, Pair<String?, String?>> = emptyMap(),
    val statusDiffMessage: String? = null
)

@Serializable
data class ReplayComparisonReport(
    val reportId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val environmentConfig: ReplayEnvironmentConfig,
    val totalSessionsReplayed: Int,
    val regressionCount: Int,
    val improvementCount: Int,
    val identicalCount: Int,
    val overallMatchPercent: Float,
    val sessionDiffs: List<SessionDiffReport> = emptyList()
)
