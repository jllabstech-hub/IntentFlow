package com.intentflow.engine.intent.runtime

import com.intentflow.core.common.dispatcher.DefaultDispatcherProvider
import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.IntentState
import com.intentflow.engine.context.ContextEngine
import com.intentflow.engine.intent.IntentEngine
import com.intentflow.engine.intent.telemetry.LocalTelemetryManager
import com.intentflow.provider.api.IntentExecutorProvider
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class IntentPipelineTest {

    private val intentEngine: IntentEngine = mockk()
    private val contextEngine: ContextEngine = mockk()
    private val executionPlanner = ExecutionPlanner()
    private val executorProvider: IntentExecutorProvider = mockk()
    private val errorHandler = RuntimeErrorHandler()
    private val telemetryManager = LocalTelemetryManager()

    private lateinit var pipeline: IntentPipeline

    private val completeObject = IntentObject(
        id = "1",
        intentId = "alarm.set",
        domain = "alarm",
        confidence = 0.90f,
        missingSlots = emptyList()
    )

    @Before
    fun setup() {
        coEvery { contextEngine.getContextSnapshot() } returns mockk(relaxed = true)
        coEvery { intentEngine.processInput(any(), any()) } returns completeObject
        coEvery { executorProvider.executeIntent(any()) } returns ExecutionResult.Success("alarm.set", "Alarm set")

        pipeline = IntentPipeline(
            intentEngine, contextEngine, executionPlanner, executorProvider,
            errorHandler, telemetryManager, DefaultDispatcherProvider()
        )
    }

    @Test
    fun testExecutePipelineRunsFullSequenceToCompletedState() = runBlocking {
        val states = pipeline.executePipeline("Set alarm for 7 AM").toList()

        assertTrue(states.isNotEmpty())
        assertTrue(states.first() is IntentState.ProcessingInput)
        assertTrue(states.last() is IntentState.Completed)

        val lastState = states.last() as IntentState.Completed
        assertTrue(lastState.result is ExecutionResult.Success)
    }
}
