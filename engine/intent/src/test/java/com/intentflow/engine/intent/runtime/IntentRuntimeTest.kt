package com.intentflow.engine.intent.runtime

import com.intentflow.core.model.ExecutionPlan
import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.ExecutionTarget
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.IntentState
import com.intentflow.engine.intent.event.DefaultPipelineEventBus
import com.intentflow.engine.intent.telemetry.LocalTelemetryManager
import com.intentflow.engine.planner.IntentPlanningEngine
import com.intentflow.engine.understanding.IntentUnderstandingEngine
import com.intentflow.provider.api.IntentExecutorProvider
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class IntentRuntimeTest {

    private val understandingEngine: IntentUnderstandingEngine = mockk()
    private val planningEngine: IntentPlanningEngine = mockk()
    private val executorProvider: IntentExecutorProvider = mockk()
    private val stateMachine = RuntimeStateMachine()
    private val errorHandler = RuntimeErrorHandler()
    private val eventBus = DefaultPipelineEventBus()
    private val telemetryManager = LocalTelemetryManager()

    private lateinit var runtime: IntentRuntime

    private val testIntentObject = IntentObject(
        id = "1",
        intentId = "alarm.set",
        domain = "alarm",
        confidence = 0.95f,
        missingSlots = emptyList()
    )

    private val testExecutionPlan = ExecutionPlan(
        intentId = "alarm.set",
        target = ExecutionTarget.ANDROID_PLUGIN,
        targetHandlerId = "plugin.telephony"
    )

    @Before
    fun setup() {
        coEvery { understandingEngine.understandInput(any(), any()) } returns testIntentObject
        coEvery { planningEngine.createPlan(any()) } returns testExecutionPlan
        coEvery { executorProvider.executeIntent(any()) } returns ExecutionResult.Success("alarm.set", "Alarm set")

        runtime = IntentRuntime(
            understandingEngine, planningEngine, executorProvider,
            stateMachine, errorHandler, eventBus, telemetryManager
        )
    }

    @Test
    fun testProcessNaturalLanguageOrchestratesAllThreeEngines() = runBlocking {
        val states = runtime.processNaturalLanguage("Set alarm for 7 AM").toList()

        assertTrue(states.isNotEmpty())
        assertTrue(states.first() is IntentState.ProcessingInput)
        assertTrue(states.last() is IntentState.Completed)
        assertEquals(IntentState.Completed(ExecutionResult.Success("alarm.set", "Alarm set")), runtime.currentState.value)
    }
}
