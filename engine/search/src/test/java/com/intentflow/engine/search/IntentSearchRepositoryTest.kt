package com.intentflow.engine.search

import com.intentflow.catalog.api.CatalogRepository
import com.intentflow.core.model.CatalogData
import com.intentflow.core.model.CatalogVersion
import com.intentflow.core.model.DomainDefinition
import com.intentflow.core.model.IntentDefinition
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class IntentSearchRepositoryTest {

    private val catalogRepository: CatalogRepository = mockk()
    private val ranker = IntentRanker()
    private val semanticSearchProvider = NoOpSemanticSearchProvider()

    private lateinit var searchRepository: DefaultIntentSearchRepository

    private val intent1 = IntentDefinition(
        intentId = "phone.call",
        name = "Make Call",
        description = "Places phone call",
        domain = "telephony",
        examples = listOf("Call Mom", "Phone John Doe")
    )

    private val intent2 = IntentDefinition(
        intentId = "alarm.set",
        name = "Set Alarm",
        description = "Sets wake up alarm",
        domain = "alarm",
        examples = listOf("Set alarm for 7 AM", "Wake me up at 6 AM")
    )

    private val testCatalog = CatalogData(
        version = CatalogVersion(1, "1.0.0"),
        domains = listOf(
            DomainDefinition(
                id = "telephony",
                displayName = "Phone",
                description = "Calling",
                intents = listOf(intent1)
            ),
            DomainDefinition(
                id = "alarm",
                displayName = "Alarm",
                description = "Alarms",
                intents = listOf(intent2)
            )
        ),
        intents = listOf(intent1, intent2)
    )

    @Before
    fun setup() {
        every { catalogRepository.activeCatalogData } returns MutableStateFlow(testCatalog)
        coEvery { catalogRepository.searchIntentsByQuery(any()) } returns listOf(intent1, intent2)

        searchRepository = DefaultIntentSearchRepository(catalogRepository, ranker, semanticSearchProvider)
    }

    @Test
    fun testSearchIntentsReturnsRankedMatches() = runBlocking {
        val results = searchRepository.searchIntents("Call Mom", limit = 5)

        assertNotNull(results)
        assertTrue(results.isNotEmpty())
        assertEquals("phone.call", results.first().intent.intentId)
        assertEquals(MatchType.EXACT, results.first().matchType)
    }

    @Test
    fun testPartialPrefixSearchIntents() = runBlocking {
        val results = searchRepository.searchIntents("Set alarm", limit = 5)

        assertTrue(results.isNotEmpty())
        val topMatch = results.find { it.intent.intentId == "alarm.set" }
        assertNotNull(topMatch)
    }
}
