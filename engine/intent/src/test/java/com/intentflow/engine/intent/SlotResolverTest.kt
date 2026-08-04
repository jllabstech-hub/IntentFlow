package com.intentflow.engine.intent

import com.intentflow.core.model.PickerType
import com.intentflow.core.model.SlotDefinition
import com.intentflow.core.model.SlotType
import com.intentflow.core.model.ValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SlotResolverTest {

    private val validator = SlotValidator()
    private val suggestionEngine = SlotSuggestionEngine()
    private val resolver = SlotResolver(validator, suggestionEngine)

    @Test
    fun testResolveSlotNormalizesAndValidates() {
        val slot = SlotDefinition(
            slotName = "time",
            displayName = "Start Time",
            slotType = SlotType.TIME,
            pickerType = PickerType.TIME_PICKER,
            required = true
        )

        val metadata = resolver.resolveSlot(slot, rawValue = "7 PM")

        assertNotNull(metadata)
        assertEquals("time", metadata.slotName)
        assertEquals("Start Time", metadata.displayName)
        assertEquals(SlotType.TIME, metadata.slotType)
        assertEquals(PickerType.TIME_PICKER, metadata.pickerType)
        assertEquals("7 PM", metadata.currentRawValue)
        assertEquals("19:00", metadata.normalizedValue)
        assertTrue(metadata.validationResult is ValidationResult.Valid)
    }

    @Test
    fun testResolveSlotDefaultValueFallback() {
        val slot = SlotDefinition(
            slotName = "state",
            displayName = "Enable WiFi",
            slotType = SlotType.BOOLEAN,
            defaultValue = "true"
        )

        val metadata = resolver.resolveSlot(slot, rawValue = null)

        assertEquals("true", metadata.currentRawValue)
        assertEquals("true", metadata.normalizedValue)
        assertTrue(metadata.validationResult is ValidationResult.Valid)
    }
}
