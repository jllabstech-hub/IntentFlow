package com.intentflow.engine.intent

import com.intentflow.core.model.SlotDefinition
import com.intentflow.core.model.SlotType
import com.intentflow.core.model.ValidationResult
import org.junit.Assert.assertTrue
import org.junit.Test

class SlotValidatorTest {

    private val validator = SlotValidator()

    @Test
    fun testValidateContact() {
        val slot = SlotDefinition("contact", "Contact", SlotType.CONTACT)
        assertTrue(validator.validateSlot(slot, "Mom") is ValidationResult.Valid)
        assertTrue(validator.validateSlot(slot, "a") is ValidationResult.Invalid)
    }

    @Test
    fun testValidateDate() {
        val slot = SlotDefinition("date", "Date", SlotType.DATE)
        assertTrue(validator.validateSlot(slot, "today") is ValidationResult.Valid)
        assertTrue(validator.validateSlot(slot, "2026-08-04") is ValidationResult.Valid)
        assertTrue(validator.validateSlot(slot, "invalid-date-xyz") is ValidationResult.Invalid)
    }

    @Test
    fun testValidateTime() {
        val slot = SlotDefinition("time", "Time", SlotType.TIME)
        assertTrue(validator.validateSlot(slot, "07:00") is ValidationResult.Valid)
        assertTrue(validator.validateSlot(slot, "7 PM") is ValidationResult.Valid)
        assertTrue(validator.validateSlot(slot, "99:99") is ValidationResult.Invalid)
    }

    @Test
    fun testValidateLocation() {
        val slot = SlotDefinition("loc", "Location", SlotType.LOCATION)
        assertTrue(validator.validateSlot(slot, "37.7749,-122.4194") is ValidationResult.Valid)
        assertTrue(validator.validateSlot(slot, "San Francisco") is ValidationResult.Valid)
    }

    @Test
    fun testValidateCurrency() {
        val slot = SlotDefinition("amount", "Amount", SlotType.CURRENCY)
        assertTrue(validator.validateSlot(slot, "$50.00") is ValidationResult.Valid)
        assertTrue(validator.validateSlot(slot, "100 USD") is ValidationResult.Valid)
        assertTrue(validator.validateSlot(slot, "abc") is ValidationResult.Invalid)
    }

    @Test
    fun testValidateImage() {
        val slot = SlotDefinition("image", "Image", SlotType.IMAGE)
        assertTrue(validator.validateSlot(slot, "photo.jpg") is ValidationResult.Valid)
        assertTrue(validator.validateSlot(slot, "content://media/external/images/1") is ValidationResult.Valid)
        assertTrue(validator.validateSlot(slot, "invalid.txt") is ValidationResult.Invalid)
    }

    @Test
    fun testValidateFile() {
        val slot = SlotDefinition("file", "File", SlotType.FILE)
        assertTrue(validator.validateSlot(slot, "/sdcard/document.pdf") is ValidationResult.Valid)
        assertTrue(validator.validateSlot(slot, "invalid_file_name_no_path") is ValidationResult.Invalid)
    }

    @Test
    fun testValidateText() {
        val slot = SlotDefinition("text", "Text", SlotType.TEXT)
        assertTrue(validator.validateSlot(slot, "Hello World") is ValidationResult.Valid)
        assertTrue(validator.validateSlot(slot, "   ") is ValidationResult.Invalid)
    }

    @Test
    fun testValidateBoolean() {
        val slot = SlotDefinition("bool", "Boolean", SlotType.BOOLEAN)
        assertTrue(validator.validateSlot(slot, "true") is ValidationResult.Valid)
        assertTrue(validator.validateSlot(slot, "yes") is ValidationResult.Valid)
        assertTrue(validator.validateSlot(slot, "maybe") is ValidationResult.Invalid)
    }

    @Test
    fun testValidateNumber() {
        val slot = SlotDefinition("num", "Number", SlotType.NUMBER)
        assertTrue(validator.validateSlot(slot, "42") is ValidationResult.Valid)
        assertTrue(validator.validateSlot(slot, "3.14") is ValidationResult.Valid)
        assertTrue(validator.validateSlot(slot, "not_a_number") is ValidationResult.Invalid)
    }

    @Test
    fun testValidateEnum() {
        val slot = SlotDefinition("enum", "Enum", SlotType.ENUM, suggestions = listOf("MISSED", "OUTGOING", "INCOMING"))
        assertTrue(validator.validateSlot(slot, "MISSED") is ValidationResult.Valid)
        assertTrue(validator.validateSlot(slot, "UNKNOWN") is ValidationResult.Invalid)
    }

    @Test
    fun testValidateMultiSelect() {
        val slot = SlotDefinition("multi", "Multi", SlotType.MULTI_SELECT, suggestions = listOf("A", "B", "C"))
        assertTrue(validator.validateSlot(slot, "A, B") is ValidationResult.Valid)
        assertTrue(validator.validateSlot(slot, "A, X") is ValidationResult.Invalid)
    }
}
