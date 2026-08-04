package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Immutable State Machine representing the lifecycle of an Intent in IntentFlow.
 */
@Serializable
sealed class IntentState {
    @Serializable
    data object Idle : IntentState()

    @Serializable
    data class ProcessingInput(val rawInput: String) : IntentState()

    @Serializable
    data class IntentIdentified(
        val intentObject: IntentObject,
        val suggestions: List<IntentDefinition> = emptyList()
    ) : IntentState()

    @Serializable
    data class SlotFilling(
        val intentObject: IntentObject,
        val activeSlotName: String? = null
    ) : IntentState()

    @Serializable
    data class ReadyToExecute(
        val intentObject: IntentObject,
        val plan: ExecutionPlan
    ) : IntentState()

    @Serializable
    data class Executing(
        val intentObject: IntentObject,
        val target: String
    ) : IntentState()

    @Serializable
    data class Completed(
        val result: ExecutionResult
    ) : IntentState()

    @Serializable
    data class Error(
        val message: String,
        val cause: String? = null
    ) : IntentState()
}
