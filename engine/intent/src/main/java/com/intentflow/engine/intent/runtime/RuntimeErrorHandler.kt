package com.intentflow.engine.intent.runtime

import com.intentflow.core.common.logger.IntentLogger
import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentState
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles exceptions and pipeline failures, mapping errors into clean IntentState.Error and ExecutionResult.Failure objects.
 */
@Singleton
class RuntimeErrorHandler @Inject constructor() {

    fun handleException(throwable: Throwable, contextMessage: String, intentId: String = "unknown"): IntentState.Error {
        IntentLogger.e("RuntimeError", "$contextMessage: ${throwable.message}", throwable)
        return IntentState.Error(
            message = "$contextMessage: ${throwable.message ?: "Unknown error"}",
            cause = throwable.cause?.message ?: throwable.javaClass.simpleName
        )
    }

    fun toExecutionFailure(throwable: Throwable, intentId: String): ExecutionResult.Failure {
        return ExecutionResult.Failure(
            intentId = intentId,
            errorMessage = throwable.message ?: "Pipeline execution failed",
            cause = throwable.javaClass.simpleName
        )
    }
}
