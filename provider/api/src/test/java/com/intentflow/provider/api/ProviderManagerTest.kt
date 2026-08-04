package com.intentflow.provider.api

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProviderManagerTest {

    private val mockProvider = OpenAiProvider()
    private val claudeProvider = ClaudeProvider()
    private val factory = ProviderFactory(mockProvider, setOf(mockProvider, claudeProvider))
    private val manager = ProviderManager(factory)

    @Test
    fun testHotSwapActiveProvider() {
        assertEquals("openai", manager.activeProvider.value.providerId)

        val success = manager.setActiveProvider("claude")
        assertTrue(success)
        assertEquals("claude", manager.activeProvider.value.providerId)
    }
}
