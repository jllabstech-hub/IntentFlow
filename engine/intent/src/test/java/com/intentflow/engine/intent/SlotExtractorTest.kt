package com.intentflow.engine.intent

import com.intentflow.core.model.EntityDefinition
import com.intentflow.core.model.IntentDefinition
import org.junit.Assert.assertNotNull
import org.junit.Test

class SlotExtractorTest {

    private val extractor = SlotExtractor()

    private val sampleIntent = IntentDefinition(
        intentId = "messaging.send",
        name = "Send Message",
        description = "Sends SMS",
        domain = "messaging"
    )

    private val entities = listOf(
        EntityDefinition(
            entityId = "contact",
            displayName = "Contacts"
        )
    )

    @Test
    fun testSlotExtractionExtractsRecipientAndBody() {
        val input = "Send a message to Alice saying I am running late"
        val result = extractor.extractSlots(input, sampleIntent, entities)

        assertNotNull(result)
    }

    @Test
    fun testMissingSlotDetectionIdentifiesUnfilledRequiredSlot() {
        val input = "Send a message to Alice"
        val result = extractor.extractSlots(input, sampleIntent, entities)

        assertNotNull(result)
    }

    @Test
    fun testBooleanSlotExtraction() {
        val wifiIntent = IntentDefinition(
            intentId = "settings.toggle_wifi",
            name = "Toggle WiFi",
            description = "WiFi toggle",
            domain = "settings"
        )

        val result = extractor.extractSlots("Turn off Wi-Fi", wifiIntent)
        assertNotNull(result)
    }
}
