package com.intentflow.dynamicui

import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.IntentSchema
import com.intentflow.core.model.PickerType
import com.intentflow.core.model.SlotDefinition
import com.intentflow.core.model.SlotType
import com.intentflow.core.model.SlotValue
import com.intentflow.core.model.UiComponentType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class UiSchemaTest {

    private val factory = DefaultUiSchemaFactory()

    private val testIntentObject = IntentObject(
        id = "1",
        intentId = "messaging.send",
        domain = "messaging",
        slots = mapOf("recipient" to SlotValue("Mom", "Mom"))
    )

    private val testSchema = IntentSchema(
        intentId = "messaging.send",
        slots = listOf(
            SlotDefinition("recipient", "Recipient", SlotType.TEXT, required = true),
            SlotDefinition("quantity", "Quantity", SlotType.NUMBER, pickerType = PickerType.STEPPER)
        )
    )

    @Test
    fun testCreateUiSchemaTranslatesComponents() {
        val uiSchema = factory.createUiSchema(testIntentObject, testSchema)

        assertNotNull(uiSchema)
        assertEquals("messaging.send", uiSchema.intentId)
        assertEquals(2, uiSchema.components.size)

        val recipientComp = uiSchema.components.first { it.slotName == "recipient" }
        assertEquals("Mom", recipientComp.currentValue)

        val stepperComp = uiSchema.components.first { it.slotName == "quantity" }
        assertEquals(UiComponentType.STEPPER, stepperComp.componentType)
    }
}
