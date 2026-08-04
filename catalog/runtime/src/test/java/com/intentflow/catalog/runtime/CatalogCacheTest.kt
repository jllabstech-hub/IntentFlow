package com.intentflow.catalog.runtime

import com.intentflow.core.model.CatalogData
import com.intentflow.core.model.CatalogVersion
import com.intentflow.core.model.DomainDefinition
import com.intentflow.core.model.EntityDefinition
import com.intentflow.core.model.IntentDefinition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogCacheTest {

    private val cache = ThreadSafeCatalogCache()

    private val testCatalog = CatalogData(
        version = CatalogVersion(1, "1.0.0"),
        domains = listOf(
            DomainDefinition(
                id = "telephony",
                displayName = "Phone & Calling",
                description = "Phone operations",
                intents = listOf(
                    IntentDefinition(
                        intentId = "phone.call",
                        name = "Call Contact",
                        description = "Places a call",
                        domain = "telephony",
                        examples = listOf("Call Mom", "Phone John")
                    )
                )
            )
        ),
        entities = listOf(
            EntityDefinition(entityId = "phone_number", displayName = "Phone Number Entity")
        )
    )

    @Test
    fun testCachePopulationAndLookup() {
        cache.populate(testCatalog)
        assertTrue(cache.isPopulated())

        val domain = cache.getDomain("telephony")
        assertNotNull(domain)
        assertEquals("Phone & Calling", domain?.displayName)

        val intent = cache.getIntent("phone.call")
        assertNotNull(intent)
        assertEquals("Call Contact", intent?.name)

        val entity = cache.getEntity("phone_number")
        assertNotNull(entity)
        assertEquals("Phone Number Entity", entity?.displayName)
    }

    @Test
    fun testUtteranceSearchInCache() {
        cache.populate(testCatalog)

        val results = cache.searchUtterances("Call Mom")
        assertEquals(1, results.size)
        assertEquals("phone.call", results.first().intentId)
    }

    @Test
    fun testClearCache() {
        cache.populate(testCatalog)
        cache.clear()

        assertNull(cache.getDomain("telephony"))
        assertNull(cache.getIntent("phone.call"))
        assertEquals(0, cache.getAllDomains().size)
    }
}
