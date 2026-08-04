package com.intentflow.core.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IntentObjectTest {

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

    @Test
    fun testIntentObjectSerialization() {
        val original = IntentObject(
            id = "test-uuid-123",
            intentId = "messaging.send",
            domain = "messaging",
            slots = mapOf("contact" to SlotValue(rawValue = "Alice", displayValue = "Alice")),
            missingSlots = emptyList(),
            confidence = 0.95f
        )

        val jsonString = json.encodeToString(original)
        val deserialized = json.decodeFromString<IntentObject>(jsonString)

        assertEquals(original, deserialized)
    }

    @Test
    fun testIsCompleteReturnsTrueWhenNoMissingSlotsAndHighConfidence() {
        val completeObj = IntentObject(
            id = "1",
            intentId = "alarm.set",
            domain = "alarm",
            missingSlots = emptyList(),
            confidence = 0.80f
        )
        assertTrue(completeObj.isComplete)
    }

    @Test
    fun testIsCompleteReturnsFalseWhenMissingSlotsExist() {
        val incompleteObj = IntentObject(
            id = "1",
            intentId = "alarm.set",
            domain = "alarm",
            missingSlots = listOf(
                SlotDefinition(slotName = "time", displayName = "Time", slotType = SlotType.TIME)
            ),
            confidence = 0.95f
        )
        assertFalse(incompleteObj.isComplete)
    }

    @Test
    fun testValidationPassesForValidObject() {
        val valid = IntentObject(
            id = "uuid-1",
            intentId = "calendar.create_event",
            domain = "calendar",
            confidence = 0.90f
        )
        assertTrue(valid.validate() is ValidationResult.Valid)
    }

    @Test
    fun testValidationFailsForBlankId() {
        val invalid = IntentObject(
            id = "",
            intentId = "messaging.send",
            domain = "messaging"
        )
        val result = invalid.validate()
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("IntentObject id cannot be blank", (result as ValidationResult.Invalid).reason)
    }

    @Test
    fun testValidationFailsForInvalidConfidence() {
        val invalid = IntentObject(
            id = "1",
            intentId = "messaging.send",
            domain = "messaging",
            confidence = 1.5f
        )
        val result = invalid.validate()
        assertTrue(result is ValidationResult.Invalid)
    }
}
