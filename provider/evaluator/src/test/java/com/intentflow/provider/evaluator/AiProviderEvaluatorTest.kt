package com.intentflow.provider.evaluator

import com.intentflow.core.model.IntentObject
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AiProviderEvaluatorTest {

    private lateinit var evaluator: AiProviderEvaluator

    private val testIntents = listOf(
        IntentObject("1", "messaging.send", "messaging"),
        IntentObject("2", "phone.call", "telephony")
    )

    @Before
    fun setup() {
        evaluator = DefaultAiProviderEvaluator()
    }

    @Test
    fun testEvaluateGemmaOnDeviceZeroCost() = runBlocking {
        val metrics = evaluator.evaluateProvider("gemma", testIntents)

        assertNotNull(metrics)
        assertEquals("gemma", metrics.providerId)
        assertEquals(0.0, metrics.estimatedCostUsd, 0.00001)
        assertEquals(12L, metrics.p50LatencyMs)
        assertEquals(99.0f, metrics.accuracyPercent)
    }

    @Test
    fun testCompareMultipleProvidersDeterminesWinner() = runBlocking {
        val report = evaluator.compareProviders(listOf("gemma", "gemini", "openai", "claude"), testIntents)

        assertNotNull(report)
        assertEquals(4, report.providerMetrics.size)
        assertEquals("gemma", report.winningProviderId) // Lowest latency on-device
    }
}
