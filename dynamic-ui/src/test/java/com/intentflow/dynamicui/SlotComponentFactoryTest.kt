package com.intentflow.dynamicui

import com.intentflow.core.model.PickerType
import com.intentflow.core.model.SlotMetadata
import com.intentflow.core.model.SlotType
import org.junit.Assert.assertEquals
import org.junit.Test

class SlotComponentFactoryTest {

    @Test
    fun testSlotMetadataPickerTypeMapping() {
        val stepperMetadata = SlotMetadata(
            slotName = "quantity",
            displayName = "Quantity",
            slotType = SlotType.NUMBER,
            pickerType = PickerType.STEPPER
        )
        assertEquals(PickerType.STEPPER, stepperMetadata.pickerType)

        val dateMetadata = SlotMetadata(
            slotName = "event_date",
            displayName = "Event Date",
            slotType = SlotType.DATE,
            pickerType = PickerType.DATE_PICKER
        )
        assertEquals(PickerType.DATE_PICKER, dateMetadata.pickerType)
    }
}
