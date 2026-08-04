package com.intentflow.catalog.runtime

import com.intentflow.core.model.CatalogData
import com.intentflow.core.model.CatalogVersion
import com.intentflow.core.model.DomainDefinition
import com.intentflow.core.model.IntentDefinition
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CatalogJsonParserTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val parser = CatalogJsonParser(json)

    @Test
    fun testParseCatalogJsonSuccess() {
        val sampleJson = """
            {
                "version": {
                    "versionCode": 1,
                    "versionName": "1.0.0"
                },
                "domains": [
                    {
                        "domainId": "messaging",
                        "displayName": "Messaging",
                        "description": "Send SMS and messages",
                        "intents": [
                            {
                                "intentId": "messaging.send",
                                "name": "Send Message",
                                "description": "Sends a message",
                                "domain": "messaging",
                                "examples": ["Send message to Bob"]
                            }
                        ]
                    }
                ],
                "entities": []
            }
        """.trimIndent()

        val catalogData = parser.parseCatalogJson(sampleJson)
        assertNotNull(catalogData)
        assertEquals(1, catalogData.version.versionCode)
        assertEquals(1, catalogData.domains.size)
        assertEquals("messaging", catalogData.domains.first().domainId)
        assertEquals(1, catalogData.domains.first().intents.size)
        assertEquals("messaging.send", catalogData.domains.first().intents.first().intentId)
    }

    @Test
    fun testSerializeAndDeserializeCatalogData() {
        val original = CatalogData(
            version = CatalogVersion(1, "1.0.0"),
            domains = listOf(
                DomainDefinition(
                    domainId = "calendar",
                    displayName = "Calendar",
                    description = "Manage events",
                    intents = listOf(
                        IntentDefinition(
                            intentId = "calendar.create",
                            name = "Create Event",
                            description = "Creates a calendar event",
                            domain = "calendar"
                        )
                    )
                )
            )
        )

        val serialized = parser.serializeCatalog(original)
        val deserialized = parser.parseCatalogJson(serialized)

        assertEquals(original, deserialized)
    }
}
