package com.intentflow.engine.execution

import com.intentflow.core.model.ExecutionPlan
import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.RetryStrategy
import com.intentflow.core.model.TimeoutPolicy
import com.intentflow.plugin.api.AndroidPlugin
import com.intentflow.plugin.api.PluginCapabilityRegistry
import com.intentflow.provider.api.ProviderManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExecutionEngineTest {

    private val pluginRegistry: PluginCapabilityRegistry = mockk(relaxed = true)
    private val providerManager: ProviderManager = mockk(relaxed = true)
    private lateinit var executionEngine: ExecutionEngine

    private val testIntent = IntentObject("1", "messaging.send", "messaging")
    private val testPlan = ExecutionPlan(intentId = "messaging.send", targetHandlerId = "plugin.telephony")

    @Before
    fun setup() {
        val mockPlugin = mockk<AndroidPlugin>(relaxed = true)
        coEvery { mockPlugin.execute(any()) } returns ExecutionResult.Success("messaging.send", "Success execution via ExecutionEngine")

        every { pluginRegistry.getPluginForIntent(any()) } returns mockPlugin

        executionEngine = DefaultExecutionEngine(pluginRegistry, providerManager)
    }

    @Test
    fun testExecutePlanWithRetriesAndTimeout() = runBlocking {
        val result = executionEngine.executePlan(
            intentObject = testIntent,
            plan = testPlan,
            timeoutPolicy = TimeoutPolicy(timeoutMs = 3000L),
            retryStrategy = RetryStrategy(maxRetries = 2)
        ).first()

        assertNotNull(result)
        assertTrue(result is ExecutionResult.Success)
        assertEquals("Success execution via ExecutionEngine", (result as ExecutionResult.Success).message)
    }

    @Test
    fun testParallelExecutionDispatch() = runBlocking {
        val tasks = listOf(
            testIntent to testPlan,
            IntentObject("2", "phone.call", "telephony") to ExecutionPlan(intentId = "phone.call", targetHandlerId = "plugin.telephony")
        )

        val results = executionEngine.executeParallel(tasks).first()

        assertEquals(2, results.size)
        assertTrue(results.all { it is ExecutionResult.Success })
    }
}
