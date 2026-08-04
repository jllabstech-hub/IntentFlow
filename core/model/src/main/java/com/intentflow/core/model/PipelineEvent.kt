package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Strongly-typed Pipeline Lifecycle Event hierarchy.
 */
@Serializable
sealed class PipelineEvent {
    abstract val eventId: String
    abstract val timestamp: Long

    @Serializable
    data class InputReceived(
        override val eventId: String,
        override val timestamp: Long = System.currentTimeMillis(),
        val rawInput: String,
        val inputSource: String = "TEXT"
    ) : PipelineEvent()

    @Serializable
    data class IntentMatched(
        override val eventId: String,
        override val timestamp: Long = System.currentTimeMillis(),
        val intentId: String,
        val confidence: Float,
        val matchType: String
    ) : PipelineEvent()

    @Serializable
    data class IntentGraphBuilt(
        override val eventId: String,
        override val timestamp: Long = System.currentTimeMillis(),
        val graphId: String,
        val nodeCount: Int,
        val edgeCount: Int
    ) : PipelineEvent()

    @Serializable
    data class ContextLoaded(
        override val eventId: String,
        override val timestamp: Long = System.currentTimeMillis(),
        val loadedProvidersCount: Int,
        val locationName: String? = null
    ) : PipelineEvent()

    @Serializable
    data class SlotsResolved(
        override val eventId: String,
        override val timestamp: Long = System.currentTimeMillis(),
        val resolvedSlotCount: Int,
        val missingSlotCount: Int
    ) : PipelineEvent()

    @Serializable
    data class ExecutionStarted(
        override val eventId: String,
        override val timestamp: Long = System.currentTimeMillis(),
        val intentId: String,
        val targetCapability: String,
        val handlerId: String
    ) : PipelineEvent()

    @Serializable
    data class ExecutionCompleted(
        override val eventId: String,
        override val timestamp: Long = System.currentTimeMillis(),
        val intentId: String,
        val durationMs: Long,
        val resultMessage: String
    ) : PipelineEvent()

    @Serializable
    data class ExecutionFailed(
        override val eventId: String,
        override val timestamp: Long = System.currentTimeMillis(),
        val intentId: String,
        val errorMessage: String,
        val cause: String
    ) : PipelineEvent()

    @Serializable
    data class ResultDisplayed(
        override val eventId: String,
        override val timestamp: Long = System.currentTimeMillis(),
        val schemaId: String,
        val totalPipelineDurationMs: Long
    ) : PipelineEvent()
}
