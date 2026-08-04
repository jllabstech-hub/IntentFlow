package com.intentflow.provider.api

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ProviderFactoryTest {

    private val mockProvider = OpenAiProvider()
    private val gemmaProvider = OpenAiProvider() // placeholder reference
    private val factory = ProviderFactory(mockProvider, setOf(mockProvider))

    @Test
    fun testGetProviderReturnsMatchingProviderOrFallback() {
        val provider = factory.getProvider("openai")
        assertNotNull(provider)
        assertEquals("openai", provider.providerId)
    }
}
