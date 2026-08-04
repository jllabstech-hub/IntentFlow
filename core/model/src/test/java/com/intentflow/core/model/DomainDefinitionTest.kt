package com.intentflow.core.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DomainDefinitionTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testDomainDefinitionSerialization() {
        val domain = DomainDefinition(
            id = "messaging",
            displayName = "Messaging",
            description = "Text and instant messaging capabilities",
            intents = listOf(
                IntentDefinition("messaging.send", "Send Message", "Sends a message", "messaging")
            )
        )

        val serialized = json.encodeToString(domain)
        val deserialized = json.decodeFromString<DomainDefinition>(serialized)

        assertEquals(domain, deserialized)
    }

    @Test
    fun testValidationPassesForValidDomain() {
        val valid = DomainDefinition(id = "weather", displayName = "Weather", description = "Weather forecasts")
        assertTrue(valid.validate() is ValidationResult.Valid)
    }

    @Test
    fun testValidationFailsForBlankId() {
        val invalid = DomainDefinition(id = "", displayName = "Weather", description = "Weather forecasts")
        assertTrue(invalid.validate() is ValidationResult.Invalid)
    }
}
