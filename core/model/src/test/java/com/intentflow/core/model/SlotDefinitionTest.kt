package com.intentflow.core.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SlotDefinitionTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testSlotDefinitionSerialization() {
        val slot = SlotDefinition(
            slotName = "phone_number",
            displayName = "Phone Number",
            slotType = SlotType.PHONE,
            required = true,
            validationRegex = "^[0-9]{10}$"
        )

        val serialized = json.encodeToString(slot)
        val deserialized = json.decodeFromString<SlotDefinition>(serialized)

        assertEquals(slot, deserialized)
    }

    @Test
    fun testValidationPassesForValidRegex() {
        val slot = SlotDefinition(
            slotName = "email",
            displayName = "Email",
            slotType = SlotType.EMAIL,
            validationRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
        )
        assertTrue(slot.validate() is ValidationResult.Valid)
    }

    @Test
    fun testValidationFailsForInvalidRegex() {
        val slot = SlotDefinition(
            slotName = "bad_slot",
            displayName = "Bad Slot",
            slotType = SlotType.TEXT,
            validationRegex = "[unclosed_regex"
        )
        val result = slot.validate()
        assertTrue(result is ValidationResult.Invalid)
    }
}
