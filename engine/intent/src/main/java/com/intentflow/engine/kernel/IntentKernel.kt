package com.intentflow.engine.kernel

import com.intentflow.catalog.api.CatalogRepository
import com.intentflow.core.common.logger.IntentLogger
import com.intentflow.core.model.ExecutionPlan
import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.ExecutionState
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.IntentState
import com.intentflow.core.model.PipelineStage
import com.intentflow.core.model.SlotValue
import com.intentflow.engine.execution.ExecutionEngine
import com.intentflow.engine.intent.runtime.RuntimeErrorHandler
import com.intentflow.engine.intent.runtime.RuntimeStateMachine
import com.intentflow.engine.planner.CapabilityExecutionPlanner
import com.intentflow.engine.planner.IntentPlanningEngine
import com.intentflow.engine.session.IntentSessionManager
import com.intentflow.engine.understanding.IntentUnderstandingEngine
import com.intentflow.plugin.api.CapabilityRegistry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 1. Intent Kernel Interface - Single Entry Point to the entire IntentFlow engine.
 */
interface IntentKernel {
    val currentState: StateFlow<IntentState>

    fun processInput(rawInput: String): Flow<IntentState>
    fun fillSlot(sessionId: String, slotName: String, slotValue: String): Flow<IntentState>
    fun executeIntent(sessionId: String): Flow<IntentState>
    fun resumeSession(sessionId: String): Flow<IntentState>
    suspend fun executeDirectly(intentObject: IntentObject): ExecutionResult
    suspend fun archiveSession(sessionId: String)
    fun shutdown()
}

/**
 * Production-ready implementation of IntentKernel coordinating all sub-engines.
 */
@Singleton
class DefaultIntentKernel @Inject constructor(
    private val catalogRepository: CatalogRepository,
    private val understandingEngine: IntentUnderstandingEngine,
    private val planningEngine: IntentPlanningEngine,
    private val capabilityPlanner: CapabilityExecutionPlanner,
    private val capabilityRegistry: CapabilityRegistry,
    private val executionEngine: ExecutionEngine,
    private val sessionManager: IntentSessionManager,
    private val stateMachine: RuntimeStateMachine,
    private val errorHandler: RuntimeErrorHandler
) : IntentKernel {

    override val currentState: StateFlow<IntentState> = stateMachine.state

    override fun processInput(rawInput: String): Flow<IntentState> = flow {
        IntentLogger.d("IntentKernel", "Processing raw input: '$rawInput'")
        emit(IntentState.ProcessingInput(rawInput))

        val session = sessionManager.createSession(rawInput)

        val intentObject = try {
            understandingEngine.understandInput(rawInput)
        } catch (e: Exception) {
            val errState = errorHandler.handleException(e, "Intent Understanding Failed", "unknown")
            emit(errState)
            return@flow
        }

        val updatedSession = session.copy(
            currentIntentObject = intentObject,
            currentPipelineStage = PipelineStage.SLOT_FILLING
        )
        sessionManager.saveSession(updatedSession)

        if (!intentObject.isComplete) {
            emit(IntentState.SlotFilling(intentObject, activeSlotName = intentObject.missingSlots.firstOrNull()?.slotName))
            return@flow
        }

        val executionPlan = planningEngine.createPlan(intentObject)
        val capabilityPlan = capabilityPlanner.resolveCapability(intentObject)

        val plannedSession = updatedSession.copy(
            executionPlan = executionPlan,
            currentPipelineStage = PipelineStage.READY_TO_EXECUTE,
            executionState = ExecutionState.AWAITING_CONFIRMATION
        )
        sessionManager.saveSession(plannedSession)

        emit(IntentState.ReadyToExecute(intentObject, executionPlan))

        emit(IntentState.Executing(intentObject, target = capabilityPlan.handlerId))
        val result = executionEngine.executePlan(intentObject, executionPlan).first()

        val completedSession = plannedSession.copy(
            currentPipelineStage = PipelineStage.COMPLETED,
            executionState = if (result is ExecutionResult.Success) ExecutionState.SUCCESS else ExecutionState.FAILURE
        )
        sessionManager.saveSession(completedSession)

        emit(IntentState.Completed(result))
    }.onEach { stateMachine.transitionTo(it) }

    override fun fillSlot(sessionId: String, slotName: String, slotValue: String): Flow<IntentState> = flow {
        val session = sessionManager.resumeSession(sessionId)
            ?: run {
                emit(IntentState.Error("Session not found: $sessionId"))
                return@flow
            }

        val currentObj = session.currentIntentObject
            ?: run {
                emit(IntentState.Error("No intent object in session $sessionId"))
                return@flow
            }

        val updatedSlots = currentObj.slots.toMutableMap()
        updatedSlots[slotName] = SlotValue(rawValue = slotValue, displayValue = slotValue)

        val remainingMissing = currentObj.missingSlots.filter { it.slotName != slotName }
        val updatedObj = currentObj.copy(
            slots = updatedSlots,
            missingSlots = remainingMissing
        )

        val updatedSession = session.copy(currentIntentObject = updatedObj)
        sessionManager.saveSession(updatedSession)

        if (!updatedObj.isComplete) {
            emit(IntentState.SlotFilling(updatedObj, activeSlotName = updatedObj.missingSlots.firstOrNull()?.slotName))
            return@flow
        }

        val executionPlan = planningEngine.createPlan(updatedObj)
        val plannedSession = updatedSession.copy(
            executionPlan = executionPlan,
            currentPipelineStage = PipelineStage.READY_TO_EXECUTE,
            executionState = ExecutionState.AWAITING_CONFIRMATION
        )
        sessionManager.saveSession(plannedSession)

        emit(IntentState.ReadyToExecute(updatedObj, executionPlan))
    }.onEach { stateMachine.transitionTo(it) }

    override fun executeIntent(sessionId: String): Flow<IntentState> = flow {
        val session = sessionManager.resumeSession(sessionId)
            ?: run {
                emit(IntentState.Error("Session not found: $sessionId"))
                return@flow
            }

        val readyObject = session.currentIntentObject
            ?: run {
                emit(IntentState.Error("No IntentObject present for execution in session $sessionId"))
                return@flow
            }

        val executionPlan = session.executionPlan
            ?: planningEngine.createPlan(readyObject)

        val capabilityPlan = capabilityPlanner.resolveCapability(readyObject)

        val updatedSession = session.copy(
            currentPipelineStage = PipelineStage.EXECUTING,
            executionState = ExecutionState.IN_PROGRESS
        )
        sessionManager.saveSession(updatedSession)

        emit(IntentState.Executing(readyObject, target = capabilityPlan.handlerId))
        val result = executionEngine.executePlan(readyObject, executionPlan).first()

        val doneSession = updatedSession.copy(
            currentPipelineStage = PipelineStage.COMPLETED,
            executionState = ExecutionState.SUCCESS
        )
        sessionManager.saveSession(doneSession)

        emit(IntentState.Completed(result))
    }.onEach { stateMachine.transitionTo(it) }

    override fun resumeSession(sessionId: String): Flow<IntentState> = flow {
        val session = sessionManager.resumeSession(sessionId)
        val intentObj = session?.currentIntentObject
        val plan = session?.executionPlan
        if (intentObj != null) {
            if (!intentObj.isComplete) {
                emit(IntentState.SlotFilling(intentObj, activeSlotName = intentObj.missingSlots.firstOrNull()?.slotName))
            } else if (plan != null) {
                emit(IntentState.ReadyToExecute(intentObj, plan))
            }
        }
    }.onEach { stateMachine.transitionTo(it) }

    override suspend fun executeDirectly(intentObject: IntentObject): ExecutionResult {
        val plan = ExecutionPlan(intentId = intentObject.intentId, targetHandlerId = "default")
        return executionEngine.executePlan(intentObject, plan).first()
    }

    override suspend fun archiveSession(sessionId: String) {
        sessionManager.archiveSession(sessionId)
    }

    override fun shutdown() {
        stateMachine.reset()
        understandingEngine.resetState()
    }
}
