package com.intentflow.core.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContextObjectTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testContextObjectSerialization() {
        val context = ContextObject(
            currentTimeString = "14:30",
            currentDateString = "2026-08-04",
            latitude = 37.7749,
            longitude = -122.4194,
            locationName = "San Francisco"
        )

        val serialized = json.encodeToString(context)
        val deserialized = json.decodeFromString<ContextObject>(serialized)

        assertEquals(context, deserialized)
    }

    @Test
    fun testValidationPassesForValidCoordinates() {
        val context = ContextObject(latitude = 40.7128, longitude = -74.0060)
        assertTrue(context.validate() is ValidationResult.Valid)
    }

    @Test
    fun testValidationFailsForInvalidLatitude() {
        val context = ContextObject(latitude = 120.0, longitude = 0.0)
        val result = context.validate()
        assertTrue(result is ValidationResult.Invalid)
    }
}
