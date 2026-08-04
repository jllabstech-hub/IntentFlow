package com.intentflow.core.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogVersionTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testCatalogVersionSerialization() {
        val version = CatalogVersion(
            versionCode = 1,
            versionName = "1.0.0",
            domainCount = 10,
            intentCount = 50,
            utteranceCount = 1000,
            checksum = "sha256-checksum-abc"
        )

        val serialized = json.encodeToString(version)
        val deserialized = json.decodeFromString<CatalogVersion>(serialized)

        assertEquals(version, deserialized)
    }

    @Test
    fun testValidationFailsForInvalidVersionCode() {
        val version = CatalogVersion(versionCode = 0, versionName = "1.0.0")
        val result = version.validate()
        assertTrue(result is ValidationResult.Invalid)
    }
}
