package com.intentflow.core.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class IntentSchemaTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testIntentSchemaSerialization() {
        val schema = IntentSchema(
            intentId = "alarm.set",
            slots = listOf(
                SlotDefinition("time", "Time", SlotType.TIME, required = true)
            ),
            validationRules = mapOf("timeFormat" to "HH:mm"),
            dependencies = listOf("time.context"),
            defaultValues = mapOf("time" to "07:00"),
            dynamicUiRules = mapOf("picker" to "time_picker"),
            contextRules = mapOf("autoFillTime" to "true"),
            executionRules = mapOf("timeoutMs" to "5000"),
            permissionRequirements = listOf("android.permission.SET_ALARM")
        )

        val serialized = json.encodeToString(schema)
        val deserialized = json.decodeFromString<IntentSchema>(serialized)

        assertNotNull(deserialized)
        assertEquals("alarm.set", deserialized.intentId)
        assertEquals(1, deserialized.slots.size)
        assertEquals("07:00", deserialized.defaultValues["time"])
        assertEquals("android.permission.SET_ALARM", deserialized.permissionRequirements.first())
    }
}
