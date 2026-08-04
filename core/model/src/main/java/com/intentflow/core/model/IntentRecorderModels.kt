package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Complete recordable interaction trace payload captured by the Intent Recorder.
 * Used for replay, regression, and bug reproduction.
 */
@Serializable
data class RecordedInteractionTrace(
    val traceId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val rawInput: String,
    val intentGraph: IntentGraph? = null,
    val intentObject: IntentObject? = null,
    val filledSlots: Map<String, SlotValue> = emptyMap(),
    val missingSlots: List<SlotDefinition> = emptyList(),
    val contextSnapshot: ContextSnapshot? = null,
    val executionPlan: ExecutionPlan? = null,
    val selectedCapability: String? = null,
    val selectedProvider: String? = null,
    val providerResponse: String? = null,
    val executionLatencyMs: Long = 0L,
    val errorTrace: String? = null,
    val isSuccess: Boolean = true,
    val metadata: Map<String, String> = emptyMap()
)

/**
 * Diff report comparing two recorded interaction traces.
 * Used by [TraceReplayer] to detect regressions or improvements between environments.
 */
@Serializable
data class TraceComparisonReport(
    val traceIdA: String,
    val traceIdB: String,
    val isIntentMatch: Boolean,
    val isSlotMatch: Boolean,
    val latencyDiffMs: Long,
    val diffSummary: List<String> = emptyList()
)
