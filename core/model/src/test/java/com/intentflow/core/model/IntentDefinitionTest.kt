package com.intentflow.core.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IntentDefinitionTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testIntentDefinitionSerialization() {
        val original = IntentDefinition(
            intentId = "messaging.send",
            name = "Send Message",
            description = "Sends an SMS or instant message",
            domain = "messaging",
            examples = listOf("Send message to Bob", "Text Mom hello")
        )

        val serialized = json.encodeToString(original)
        val deserialized = json.decodeFromString<IntentDefinition>(serialized)

        assertEquals(original, deserialized)
    }

    @Test
    fun testValidationPassesForValidIntentDefinition() {
        val valid = IntentDefinition(
            intentId = "phone.call",
            name = "Make Call",
            description = "Places a phone call",
            domain = "phone"
        )
        assertTrue(valid.validate() is ValidationResult.Valid)
    }

    @Test
    fun testValidationFailsForBlankIntentId() {
        val invalid = IntentDefinition(
            intentId = "   ",
            name = "Make Call",
            description = "Places a phone call",
            domain = "phone"
        )
        val result = invalid.validate()
        assertTrue(result is ValidationResult.Invalid)
    }
}
