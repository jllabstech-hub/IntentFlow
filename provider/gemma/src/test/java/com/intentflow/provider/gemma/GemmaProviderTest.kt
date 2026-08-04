package com.intentflow.provider.gemma

import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GemmaProviderTest {

    private val modelManager: GemmaModelManager = mockk()
    private lateinit var gemmaProvider: GemmaProvider

    private val testIntentObject = IntentObject(
        id = "1",
        intentId = "messaging.send",
        domain = "messaging"
    )

    @Before
    fun setup() {
        gemmaProvider = GemmaProvider(modelManager)
    }

    @Test
    fun testExecuteFailsWhenModelNotDownloaded() = runBlocking {
        every { modelManager.isModelDownloaded() } returns false

        val result = gemmaProvider.executeIntent(testIntentObject)
        assertTrue(result is ExecutionResult.Failure)
        assertEquals("MODEL_NOT_DOWNLOADED", (result as ExecutionResult.Failure).cause)
    }

    @Test
    fun testExecuteSucceedsWhenModelDownloaded() = runBlocking {
        every { modelManager.isModelDownloaded() } returns true

        val result = gemmaProvider.executeIntent(testIntentObject)
        assertTrue(result is ExecutionResult.Success)
        assertEquals("messaging.send", (result as ExecutionResult.Success).intentId)
    }
}
