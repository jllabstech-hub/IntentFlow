package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Execution strategy mode for intent steps and graph nodes.
 * Shared across execution engine and intent graph models.
 */
@Serializable
enum class ExecutionMode {
    SEQUENTIAL,
    PARALLEL,
    CONDITIONAL
}

@Serializable
data class TimeoutPolicy(
    val timeoutMs: Long = 5000L,
    val cancelOnTimeout: Boolean = true
)

@Serializable
data class RetryStrategy(
    val maxRetries: Int = 3,
    val initialBackoffMs: Long = 500L,
    val backoffMultiplier: Double = 2.0
)

@Serializable
data class ExecutionProgress(
    val executionId: String,
    val intentId: String,
    val currentStepIndex: Int,
    val totalSteps: Int,
    val progressPercent: Float,
    val statusMessage: String
)

/**
 * Sealed class representing the structured result of a completed intent execution.
 *
 * Used by [IntentState.Completed], all plugin implementations, and all provider implementations.
 *
 * Usage:
 * ```kotlin
 * ExecutionResult.Success(intentId = "...", message = "Done")
 * ExecutionResult.Failure(intentId = "...", errorMessage = "Something went wrong")
 * ```
 */
@Serializable
sealed class ExecutionResult {

    /** Successful execution with optional output data. */
    @Serializable
    data class Success(
        val intentId: String,
        val message: String,
        val domain: String = "",
        val providerId: String = "",
        val outputData: Map<String, String> = emptyMap(),
        val latencyMs: Long = 0L,
        val metadata: Map<String, String> = emptyMap(),
        val timestamp: Long = System.currentTimeMillis()
    ) : ExecutionResult()

    /** Failed execution with error information. */
    @Serializable
    data class Failure(
        val intentId: String,
        val errorMessage: String,
        val domain: String = "",
        val cause: String? = null,
        val providerId: String = "",
        val latencyMs: Long = 0L,
        val timestamp: Long = System.currentTimeMillis()
    ) : ExecutionResult()
}
