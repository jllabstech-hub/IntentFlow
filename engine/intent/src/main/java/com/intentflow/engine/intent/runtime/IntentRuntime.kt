package com.intentflow.engine.intent.runtime

import com.intentflow.core.model.ExecutionPlan
import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.IntentState
import com.intentflow.core.model.PipelineEvent
import com.intentflow.engine.intent.event.PipelineEventBus
import com.intentflow.engine.intent.telemetry.LocalTelemetryManager
import com.intentflow.engine.planner.IntentPlanningEngine
import com.intentflow.engine.understanding.IntentUnderstandingEngine
import com.intentflow.provider.api.IntentExecutorProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface contract for the Intent Runtime Engine.
 */
interface IntentRuntimeEngine {
    val currentState: StateFlow<IntentState>
    fun executePlan(intentObject: IntentObject, plan: ExecutionPlan): Flow<IntentState>
    suspend fun executeDirectly(intentObject: IntentObject): ExecutionResult
    fun cancelExecution()
}

/**
 * Public Facade Entry Point for the Intent Runtime Engine with Pipeline Event Bus publishing.
 */
@Singleton
class IntentRuntime @Inject constructor(
    private val understandingEngine: IntentUnderstandingEngine,
    private val planningEngine: IntentPlanningEngine,
    private val executorProvider: IntentExecutorProvider,
    private val stateMachine: RuntimeStateMachine,
    private val errorHandler: RuntimeErrorHandler,
    private val eventBus: PipelineEventBus,
    val telemetryManager: LocalTelemetryManager
) : IntentRuntimeEngine {

    override val currentState: StateFlow<IntentState> = stateMachine.state

    fun processNaturalLanguage(input: String): Flow<IntentState> = flow {
        val startTime = System.currentTimeMillis()
        val runId = UUID.randomUUID().toString()

        try {
            eventBus.publish(PipelineEvent.InputReceived(eventId = "evt_in_$runId", rawInput = input))
            emit(IntentState.ProcessingInput(input))

            // 1. Understanding
            val intentObject = understandingEngine.understandInput(input)
            eventBus.publish(
                PipelineEvent.IntentMatched(
                    eventId = "evt_match_$runId",
                    intentId = intentObject.intentId,
                    confidence = intentObject.confidence,
                    matchType = "RULE_BASED"
                )
            )

            eventBus.publish(
                PipelineEvent.SlotsResolved(
                    eventId = "evt_slots_$runId",
                    resolvedSlotCount = intentObject.slots.size,
                    missingSlotCount = intentObject.missingSlots.size
                )
            )

            if (!intentObject.isComplete) {
                emit(IntentState.SlotFilling(intentObject, activeSlotName = intentObject.missingSlots.firstOrNull()?.slotName))
                return@flow
            }

            // 2. Planning
            val plan = planningEngine.createPlan(intentObject)
            eventBus.publish(
                PipelineEvent.ExecutionStarted(
                    eventId = "evt_plan_$runId",
                    intentId = intentObject.intentId,
                    targetCapability = plan.targetHandlerId,
                    handlerId = plan.targetHandlerId
                )
            )

            emit(IntentState.ReadyToExecute(intentObject, plan))

            // 3. Execution
            emit(IntentState.Executing(intentObject, target = plan.targetHandlerId))
            val result = executorProvider.executeIntent(intentObject)

            val latency = System.currentTimeMillis() - startTime
            val msg = if (result is ExecutionResult.Success) result.message else (result as? ExecutionResult.Failure)?.errorMessage ?: "Failed"
            eventBus.publish(
                PipelineEvent.ExecutionCompleted(
                    eventId = "evt_exec_$runId",
                    intentId = intentObject.intentId,
                    durationMs = latency,
                    resultMessage = msg
                )
            )

            telemetryManager.logPipelineStep(
                stepName = "Execution",
                intentId = intentObject.intentId,
                durationMs = latency,
                success = result is ExecutionResult.Success
            )

            emit(IntentState.Completed(result))

        } catch (e: Exception) {
            val errorState = errorHandler.handleException(e, "Runtime Pipeline Exception", input)
            emit(errorState)
        }
    }.onEach { stateMachine.transitionTo(it) }

    fun resumeSlotFilling(intentObject: IntentObject, slotName: String, slotValue: String): Flow<IntentState> = flow {
        val startTime = System.currentTimeMillis()
        val runId = UUID.randomUUID().toString()

        try {
            val updatedIntent = understandingEngine.updateSlot(intentObject, slotName, slotValue)
            eventBus.publish(
                PipelineEvent.SlotsResolved(
                    eventId = "evt_slots_update_$runId",
                    resolvedSlotCount = updatedIntent.slots.size,
                    missingSlotCount = updatedIntent.missingSlots.size
                )
            )

            if (!updatedIntent.isComplete) {
                emit(IntentState.SlotFilling(updatedIntent, activeSlotName = updatedIntent.missingSlots.firstOrNull()?.slotName))
                return@flow
            }

            val plan = planningEngine.createPlan(updatedIntent)
            emit(IntentState.ReadyToExecute(updatedIntent, plan))

            emit(IntentState.Executing(updatedIntent, target = plan.targetHandlerId))
            val result = executorProvider.executeIntent(updatedIntent)

            val latency = System.currentTimeMillis() - startTime
            telemetryManager.logPipelineStep(
                stepName = "ResumeSlotFilling",
                intentId = updatedIntent.intentId,
                durationMs = latency,
                success = result is ExecutionResult.Success
            )

            emit(IntentState.Completed(result))

        } catch (e: Exception) {
            val errorState = errorHandler.handleException(e, "Resume Runtime Exception", intentObject.intentId)
            emit(errorState)
        }
    }.onEach { stateMachine.transitionTo(it) }

    override fun executePlan(intentObject: IntentObject, plan: ExecutionPlan): Flow<IntentState> = flow {
        emit(IntentState.Executing(intentObject, target = plan.targetHandlerId))
        val result = executorProvider.executeIntent(intentObject)
        emit(IntentState.Completed(result))
    }.onEach { stateMachine.transitionTo(it) }

    override suspend fun executeDirectly(intentObject: IntentObject): ExecutionResult {
        return executorProvider.executeIntent(intentObject)
    }

    override fun cancelExecution() {
        stateMachine.reset()
    }

    fun reset() {
        stateMachine.reset()
        understandingEngine.resetState()
    }
}
