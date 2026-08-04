package com.intentflow.dynamicui

import com.intentflow.core.model.PipelineTraceInspector
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class PipelineTraceInspectorTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testPipelineTraceInspectorSerialization() {
        val trace = PipelineTraceInspector(
            rawInput = "Set alarm for 7 AM",
            detectedIntentId = "alarm.set",
            confidence = 0.95f,
            extractedSlots = mapOf("time" to "07:00"),
            missingSlots = emptyList(),
            contextSummary = "Time: 14:30, Location: San Francisco",
            executionJson = "{\"intentId\":\"alarm.set\",\"time\":\"07:00\"}",
            providerId = "plugin.telephony",
            latencyMs = 12L,
            outputMessage = "Alarm set for 7:00 AM",
            isSuccess = true
        )

        val serialized = json.encodeToString(trace)
        val deserialized = json.decodeFromString<PipelineTraceInspector>(serialized)

        assertNotNull(deserialized)
        assertEquals("alarm.set", deserialized.detectedIntentId)
        assertEquals(12L, deserialized.latencyMs)
        assertEquals(true, deserialized.isSuccess)
    }
}
