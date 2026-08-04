package com.intentflow.engine.execution

import com.intentflow.core.model.ExecutionPlan
import com.intentflow.core.model.ExecutionProgress
import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.RetryStrategy
import com.intentflow.core.model.TimeoutPolicy
import com.intentflow.plugin.api.PluginCapabilityRegistry
import com.intentflow.provider.api.ProviderManager
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeoutOrNull
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Execution Engine Interface.
 * Manages task queuing, retries, parallel & sequential dispatching, cancellation, and error recovery.
 */
interface ExecutionEngine {
    val activeProgress: StateFlow<ExecutionProgress?>

    fun executePlan(
        intentObject: IntentObject,
        plan: ExecutionPlan,
        timeoutPolicy: TimeoutPolicy = TimeoutPolicy(),
        retryStrategy: RetryStrategy = RetryStrategy()
    ): Flow<ExecutionResult>

    fun executeParallel(
        tasks: List<Pair<IntentObject, ExecutionPlan>>
    ): Flow<List<ExecutionResult>>

    fun cancelExecution(executionId: String)
}

/**
 * Production-ready implementation of ExecutionEngine.
 */
@Singleton
class DefaultExecutionEngine @Inject constructor(
    private val pluginRegistry: PluginCapabilityRegistry,
    private val providerManager: ProviderManager
) : ExecutionEngine {

    private val _activeProgress = MutableStateFlow<ExecutionProgress?>(null)
    override val activeProgress: StateFlow<ExecutionProgress?> = _activeProgress.asStateFlow()

    private val cancelledSet = java.util.Collections.newSetFromMap(java.util.concurrent.ConcurrentHashMap<String, Boolean>())

    override fun executePlan(
        intentObject: IntentObject,
        plan: ExecutionPlan,
        timeoutPolicy: TimeoutPolicy,
        retryStrategy: RetryStrategy
    ): Flow<ExecutionResult> = flow {
        val execId = UUID.randomUUID().toString()
        _activeProgress.value = ExecutionProgress(
            executionId = execId,
            intentId = intentObject.intentId,
            currentStepIndex = 1,
            totalSteps = 1,
            progressPercent = 0.0f,
            statusMessage = "Starting execution for ${intentObject.intentId}"
        )

        var lastResult: ExecutionResult = ExecutionResult.Failure(intentObject.intentId, "Execution failed")

        for (attempt in 1..retryStrategy.maxRetries) {
            if (cancelledSet.contains(execId)) {
                emit(ExecutionResult.Failure(intentObject.intentId, "Execution cancelled by user"))
                return@flow
            }

            _activeProgress.value = _activeProgress.value?.copy(
                progressPercent = (attempt.toFloat() / retryStrategy.maxRetries) * 100f,
                statusMessage = "Executing attempt $attempt of ${retryStrategy.maxRetries}"
            )

            val timedResult = withTimeoutOrNull(timeoutPolicy.timeoutMs) {
                // Attempt execution via Plugin Registry first, then Provider Manager
                val plugin = pluginRegistry.getPluginForIntent(intentObject.intentId)
                if (plugin != null) {
                    plugin.execute(intentObject)
                } else {
                    providerManager.activeProvider.value.executeIntent(intentObject)
                }
            }

            if (timedResult is ExecutionResult.Success) {
                _activeProgress.value = _activeProgress.value?.copy(progressPercent = 100.0f, statusMessage = "Completed successfully")
                emit(timedResult)
                return@flow
            } else if (timedResult != null) {
                lastResult = timedResult
            } else {
                lastResult = ExecutionResult.Failure(intentObject.intentId, "Execution timed out after ${timeoutPolicy.timeoutMs} ms")
            }
        }

        _activeProgress.value = _activeProgress.value?.copy(statusMessage = "Execution failed after retries")
        emit(lastResult)
    }

    override fun executeParallel(
        tasks: List<Pair<IntentObject, ExecutionPlan>>
    ): Flow<List<ExecutionResult>> = flow {
        val results = coroutineScope {
            tasks.map { (intentObj, plan) ->
                async {
                    val plugin = pluginRegistry.getPluginForIntent(intentObj.intentId)
                    if (plugin != null) {
                        plugin.execute(intentObj)
                    } else {
                        providerManager.activeProvider.value.executeIntent(intentObj)
                    }
                }
            }.map { it.await() }
        }
        emit(results)
    }

    override fun cancelExecution(executionId: String) {
        cancelledSet.add(executionId)
    }
}
