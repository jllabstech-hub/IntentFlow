package com.intentflow.engine.intent.runtime

import com.intentflow.core.common.dispatcher.DispatcherProvider
import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.IntentState
import com.intentflow.engine.context.ContextEngine
import com.intentflow.engine.intent.IntentEngine
import com.intentflow.engine.intent.telemetry.LocalTelemetryManager
import com.intentflow.provider.api.IntentExecutorProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 7-Step Intent Runtime Pipeline:
 * User Input → Intent Search → Intent Match → Slot Resolution → Context Enrichment → Execution Planning → Provider Execution → Result
 */
@Singleton
class IntentPipeline @Inject constructor(
    private val intentEngine: IntentEngine,
    private val contextEngine: ContextEngine,
    private val executionPlanner: ExecutionPlanner,
    private val executorProvider: IntentExecutorProvider,
    private val errorHandler: RuntimeErrorHandler,
    private val telemetryManager: LocalTelemetryManager,
    private val dispatchers: DispatcherProvider
) {

    fun executePipeline(userInput: String): Flow<IntentState> = flow {
        val startTime = System.currentTimeMillis()
        try {
            // Step 1: User Input
            emit(IntentState.ProcessingInput(userInput))

            // Step 2 & 3: Intent Search & Intent Match
            val contextSnapshot = contextEngine.getContextSnapshot() // Step 5: Context Enrichment
            val intentObject = intentEngine.processInput(userInput, contextSnapshot)

            telemetryManager.logPipelineStep(
                stepName = "IntentDetection",
                intentId = intentObject.intentId,
                durationMs = System.currentTimeMillis() - startTime,
                success = intentObject.intentId != "unknown.fallback"
            )

            // Step 4: Slot Resolution & Completeness Check
            if (!intentObject.isComplete) {
                emit(IntentState.SlotFilling(intentObject, activeSlotName = intentObject.missingSlots.firstOrNull()?.slotName))
                return@flow
            }

            // Step 6: Execution Planning
            val plan = executionPlanner.buildExecutionPlan(intentObject)
            val readyObject = intentObject.copy(executionPlan = plan)
            emit(IntentState.ReadyToExecute(readyObject, plan))

            // Step 7: Provider Execution → Result
            emit(IntentState.Executing(readyObject, target = plan.targetHandlerId))
            val executionStartTime = System.currentTimeMillis()

            val result = executorProvider.executeIntent(readyObject)

            telemetryManager.logPipelineStep(
                stepName = "ProviderExecution",
                intentId = readyObject.intentId,
                durationMs = System.currentTimeMillis() - executionStartTime,
                success = result is ExecutionResult.Success
            )

            emit(IntentState.Completed(result))

        } catch (e: Exception) {
            val errorState = errorHandler.handleException(e, "Pipeline Execution Exception")
            emit(errorState)
        }
    }.flowOn(dispatchers.default)

    fun resumePipelineWithSlot(intentObject: IntentObject, slotName: String, slotValue: String): Flow<IntentState> = flow {
        val startTime = System.currentTimeMillis()
        try {
            val updatedObject = intentEngine.updateSlot(intentObject, slotName, slotValue)

            if (!updatedObject.isComplete) {
                emit(IntentState.SlotFilling(updatedObject, activeSlotName = updatedObject.missingSlots.firstOrNull()?.slotName))
                return@flow
            }

            val plan = executionPlanner.buildExecutionPlan(updatedObject)
            val readyObject = updatedObject.copy(executionPlan = plan)
            emit(IntentState.ReadyToExecute(readyObject, plan))

            emit(IntentState.Executing(readyObject, target = plan.targetHandlerId))
            val result = executorProvider.executeIntent(readyObject)

            telemetryManager.logPipelineStep(
                stepName = "ResumeProviderExecution",
                intentId = readyObject.intentId,
                durationMs = System.currentTimeMillis() - startTime,
                success = result is ExecutionResult.Success
            )

            emit(IntentState.Completed(result))

        } catch (e: Exception) {
            val errorState = errorHandler.handleException(e, "Resume Pipeline Exception", intentObject.intentId)
            emit(errorState)
        }
    }.flowOn(dispatchers.default)
}
