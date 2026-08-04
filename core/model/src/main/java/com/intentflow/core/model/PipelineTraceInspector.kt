package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Complete step-by-step Developer Inspector Trace model tracking:
 * Raw Input → Detected Intent → Extracted Slots → Context → Execution JSON → Provider → Latency → Output
 */
@Serializable
data class PipelineTraceInspector(
    val rawInput: String,
    val detectedIntentId: String?,
    val confidence: Float,
    val extractedSlots: Map<String, String>,
    val missingSlots: List<String>,
    val contextSummary: String,
    val executionJson: String,
    val providerId: String,
    val latencyMs: Long,
    val outputMessage: String,
    val isSuccess: Boolean
)
