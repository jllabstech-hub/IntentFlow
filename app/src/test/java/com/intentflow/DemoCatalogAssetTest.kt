package com.intentflow

import com.intentflow.catalog.runtime.CatalogJsonParser
import com.intentflow.catalog.runtime.DefaultCatalogValidator
import com.intentflow.core.model.ValidationResult
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class DemoCatalogAssetTest {

    private val jsonParser = CatalogJsonParser(Json { ignoreUnknownKeys = true })
    private val validator = DefaultCatalogValidator()

    @Test
    fun testDemoCatalogAssetValidityAndCounts() {
        val assetFile = File("src/main/assets/catalog/catalog_v1.json")
        assertTrue("catalog_v1.json asset file must exist", assetFile.exists())

        val jsonString = assetFile.readText()
        val catalogData = jsonParser.parseCatalogJson(jsonString)

        assertNotNull(catalogData)
        assertEquals(10, catalogData.domains.size)
        assertEquals(50, catalogData.intentCount)
        assertEquals(1000, catalogData.utteranceCount)

        // Validate every intent has deep links, metadata, and example utterances
        for (domain in catalogData.domains) {
            assertEquals(5, domain.intents.size)
            for (intent in domain.intents) {
                assertNotNull("deepLink is required", intent.deepLink)
                assertTrue("deepLink must start with intentflow://", intent.deepLink!!.startsWith("intentflow://"))
                assertTrue("metadata must not be empty", intent.metadata.isNotEmpty())
                assertEquals(20, intent.exampleUtterances.size)
            }
        }

        // Validate whole catalog
        val validationResult = validator.validate(catalogData)
        assertTrue("Catalog validation must pass", validationResult is ValidationResult.Valid)
    }
}
