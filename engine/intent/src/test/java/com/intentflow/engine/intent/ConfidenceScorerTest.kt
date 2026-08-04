package com.intentflow.engine.intent

import com.intentflow.core.model.IntentDefinition
import com.intentflow.core.model.SlotDefinition
import com.intentflow.core.model.SlotType
import com.intentflow.core.model.SlotValue
import org.junit.Assert.assertEquals
import org.junit.Test

class ConfidenceScorerTest {

    private val scorer = ConfidenceScorer()

    private val intent = IntentDefinition(
        intentId = "alarm.set",
        name = "Set Alarm",
        description = "Sets alarm",
        domain = "alarm"
    )

    @Test
    fun testConfidenceCalculationWhenAllRequiredSlotsFilled() {
        val extracted = mapOf("time" to SlotValue("07:00", "07:00"))
        val score = scorer.calculateConfidence(0.90f, intent, extracted)

        assertEquals(0.90f, score, 0.01f)
    }

    @Test
    fun testConfidenceCalculationWhenRequiredSlotMissing() {
        val extracted = emptyMap<String, SlotValue>()
        val score = scorer.calculateConfidence(0.90f, intent, extracted)

        assertEquals(0.90f, score, 0.01f)
    }
}
