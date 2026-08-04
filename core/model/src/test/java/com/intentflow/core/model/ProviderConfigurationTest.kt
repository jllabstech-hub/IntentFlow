package com.intentflow.core.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProviderConfigurationTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testProviderConfigurationSerialization() {
        val config = ProviderConfiguration(
            providerId = "gemini",
            displayName = "Google Gemini Provider",
            isOfflineCapable = false,
            apiKey = "test-api-key",
            modelName = "gemini-1.5-flash"
        )

        val serialized = json.encodeToString(config)
        val deserialized = json.decodeFromString<ProviderConfiguration>(serialized)

        assertEquals(config, deserialized)
    }

    @Test
    fun testValidationFailsForInvalidTemperature() {
        val config = ProviderConfiguration(
            providerId = "gemma",
            displayName = "Gemma Local",
            temperature = 3.5f
        )
        val result = config.validate()
        assertTrue(result is ValidationResult.Invalid)
    }
}
