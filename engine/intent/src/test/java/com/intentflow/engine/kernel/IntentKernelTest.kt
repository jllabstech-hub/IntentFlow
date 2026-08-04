package com.intentflow.engine.kernel

import com.intentflow.catalog.api.CatalogRepository
import com.intentflow.core.model.CapabilityExecutionPlan
import com.intentflow.core.model.ExecutionPlan
import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.ExecutionTargetCapability
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.IntentState
import com.intentflow.engine.execution.ExecutionEngine
import com.intentflow.engine.intent.runtime.RuntimeErrorHandler
import com.intentflow.engine.intent.runtime.RuntimeStateMachine
import com.intentflow.engine.planner.CapabilityExecutionPlanner
import com.intentflow.engine.planner.IntentPlanningEngine
import com.intentflow.engine.session.IntentSessionManager
import com.intentflow.engine.understanding.IntentUnderstandingEngine
import com.intentflow.plugin.api.CapabilityRegistry
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class IntentKernelTest {

    private val catalogRepository: CatalogRepository = mockk(relaxed = true)
    private val understandingEngine: IntentUnderstandingEngine = mockk(relaxed = true)
    private val planningEngine: IntentPlanningEngine = mockk(relaxed = true)
    private val capabilityPlanner: CapabilityExecutionPlanner = mockk(relaxed = true)
    private val capabilityRegistry: CapabilityRegistry = mockk(relaxed = true)
    private val executionEngine: ExecutionEngine = mockk(relaxed = true)
    private val sessionManager: IntentSessionManager = mockk(relaxed = true)
    private val stateMachine = RuntimeStateMachine()
    private val errorHandler = RuntimeErrorHandler()

    private lateinit var kernel: IntentKernel

    private val testIntentObject = IntentObject(
        id = "1",
        intentId = "messaging.send",
        domain = "messaging",
        confidence = 0.95f,
        missingSlots = emptyList()
    )

    private val testExecPlan = ExecutionPlan("messaging.send", targetHandlerId = "plugin.telephony")

    private val testCapPlan = CapabilityExecutionPlan(
        intentId = "messaging.send",
        selectedCapability = ExecutionTargetCapability.ANDROID_PLUGIN,
        handlerId = "plugin.telephony"
    )

    @Before
    fun setup() {
        coEvery { sessionManager.createSession(any()) } returns mockk(relaxed = true)
        coEvery { understandingEngine.understandInput(any(), any()) } returns testIntentObject
        coEvery { planningEngine.createPlan(any()) } returns testExecPlan
        coEvery { capabilityPlanner.resolveCapability(any()) } returns testCapPlan
        coEvery { executionEngine.executePlan(any(), any(), any(), any()) } returns flowOf(ExecutionResult.Success("messaging.send", "Message sent"))

        kernel = DefaultIntentKernel(
            catalogRepository, understandingEngine, planningEngine,
            capabilityPlanner, capabilityRegistry, executionEngine,
            sessionManager, stateMachine, errorHandler
        )
    }

    @Test
    fun testDispatchNaturalLanguageOrchestratesSystemViaKernel() = runBlocking {
        val states = kernel.processInput("Send message to Mom").toList()

        assertTrue(states.isNotEmpty())
        assertTrue(states.first() is IntentState.ProcessingInput)
        assertTrue(states.last() is IntentState.Completed)
        assertEquals(IntentState.Completed(ExecutionResult.Success("messaging.send", "Message sent")), kernel.currentState.value)
    }
}
