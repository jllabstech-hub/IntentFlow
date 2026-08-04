package com.intentflow.provider.api

import com.intentflow.core.common.dispatcher.DefaultDispatcherProvider
import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProviderExecutionPipelineTest {

    private val mockProvider = OpenAiProvider()
    private val factory = ProviderFactory(mockProvider, setOf(mockProvider))
    private val manager = ProviderManager(factory)

    private lateinit var pipeline: ProviderExecutionPipeline

    @Before
    fun setup() {
        pipeline = ProviderExecutionPipeline(manager, DefaultDispatcherProvider())
    }

    @Test
    fun testExecutePipelineRoutesToActiveProvider() = runBlocking {
        val intentObject = IntentObject(
            id = "1",
            intentId = "messaging.send",
            domain = "messaging"
        )

        val result = pipeline.execute(intentObject)
        assertTrue(result is ExecutionResult.Success)
        assertEquals("messaging.send", (result as ExecutionResult.Success).intentId)
    }
}
