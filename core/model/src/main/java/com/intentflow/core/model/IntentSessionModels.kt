package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Stages in the IntentFlow execution pipeline.
 */
@Serializable
enum class PipelineStage {
    INPUT_RECEIVED,
    INTENT_UNDERSTOOD,
    GRAPH_BUILT,
    SLOT_FILLING,
    CONTEXT_ENRICHED,
    READY_TO_EXECUTE,
    EXECUTING,
    COMPLETED,
    FAILED,
    SUSPENDED
}

/**
 * State of intent execution.
 */
@Serializable
enum class ExecutionState {
    NOT_STARTED,
    AWAITING_SLOTS,
    AWAITING_CONFIRMATION,
    IN_PROGRESS,
    SUCCESS,
    FAILURE,
    PAUSED
}

/**
 * Persistent, serializable session object holding complete interaction state.
 */
@Serializable
data class IntentSession(
    val sessionId: String,
    val userInputHistory: List<String> = emptyList(),
    val intentGraph: IntentGraph? = null,
    val currentIntentObject: IntentObject? = null,
    val filledSlots: Map<String, SlotValue> = emptyMap(),
    val missingSlots: List<SlotDefinition> = emptyList(),
    val currentPipelineStage: PipelineStage = PipelineStage.INPUT_RECEIVED,
    val contextSnapshot: ContextSnapshot? = null,
    val executionPlan: ExecutionPlan? = null,
    val executionState: ExecutionState = ExecutionState.NOT_STARTED,
    val timestamp: Long = System.currentTimeMillis(),
    val version: Int = 1,
    val metadata: Map<String, String> = emptyMap()
)
