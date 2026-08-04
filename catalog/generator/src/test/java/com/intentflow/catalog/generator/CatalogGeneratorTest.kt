package com.intentflow.catalog.generator

import com.intentflow.core.model.CatalogGenerationOptions
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CatalogGeneratorTest {

    private lateinit var generator: DefaultCatalogGenerator

    @Before
    fun setup() {
        generator = DefaultCatalogGenerator()
    }

    @Test
    fun testUtteranceDeduplicationAndSlotNormalization() {
        val raw = listOf("Send Text ", "send text", "SEND TEXT")
        val deduplicated = generator.deduplicateUtterances(raw)

        assertEquals(1, deduplicated.size)
        assertEquals("send text", deduplicated.first())

        val normalizedSlot = generator.normalizeSlotNames(" Home Airport ")
        assertEquals("home_airport", normalizedSlot)
    }

    @Test
    fun testCatalogGenerationAndDiffEngine() = runBlocking {
        val options = CatalogGenerationOptions(targetVersionCode = 2, targetVersionName = "2.0.0")
        val catalog = generator.generateCatalog(emptyList(), options)

        assertNotNull(catalog)
        assertEquals(2, catalog.version.versionCode)

        val diff = generator.computeDiff(catalog, catalog)
        assertFalse(diff.isBreakingChange)
    }
}
