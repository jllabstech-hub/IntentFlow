package com.intentflow.engine.understanding

import com.intentflow.catalog.api.CatalogRepository
import com.intentflow.core.model.IntentDefinition
import com.intentflow.core.model.IntentObject
import com.intentflow.engine.graph.IntentGraphEngine
import com.intentflow.engine.intent.ConfidenceScorer
import com.intentflow.engine.intent.SlotExtractor
import com.intentflow.engine.intent.learning.LearningEngine
import com.intentflow.engine.planner.IntentPlanner
import com.intentflow.engine.search.IntentSearchRepository
import com.intentflow.engine.search.MatchType
import com.intentflow.engine.search.ScoredIntentMatch
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class IntentUnderstandingEngineTest {

    private val catalogRepository: CatalogRepository = mockk()
    private val searchRepository: IntentSearchRepository = mockk()
    private val slotExtractor: SlotExtractor = mockk()
    private val confidenceScorer: ConfidenceScorer = mockk()
    private val learningEngine: LearningEngine = mockk(relaxed = true)
    private val graphEngine: IntentGraphEngine = mockk(relaxed = true)
    private val intentPlanner: IntentPlanner = mockk()

    private lateinit var engine: IntentUnderstandingEngine

    private val intentDef = IntentDefinition("alarm.set", "Set Alarm", "Desc", "alarm")

    @Before
    fun setup() {
        coEvery { searchRepository.searchIntents(any(), any()) } returns listOf(
            ScoredIntentMatch(intentDef, score = 0.95f, matchType = MatchType.EXACT)
        )
        every { learningEngine.personalizeRanking(any()) } answers { firstArg() }
        coEvery { intentPlanner.planGraph(any()) } returns listOf(
            IntentObject(id = "1", intentId = "alarm.set", domain = "alarm", confidence = 0.95f)
        )
        coEvery { catalogRepository.getIntentById("alarm.set") } returns intentDef
        every { catalogRepository.activeCatalogData.value } returns mockk(relaxed = true)
        every { slotExtractor.extractSlots(any(), any(), any()) } returns mockk(relaxed = true)
        every { confidenceScorer.calculateConfidence(any(), any(), any()) } returns 0.95f

        engine = RuleBasedIntentUnderstandingEngine(
            catalogRepository, searchRepository, slotExtractor,
            confidenceScorer, learningEngine, graphEngine, intentPlanner
        )
    }

    @Test
    fun testUnderstandInputProducesIntentObject() = runBlocking {
        val intentObject = engine.understandInput("Set alarm for 7 AM")

        assertNotNull(intentObject)
        assertEquals("alarm.set", intentObject.intentId)
        assertEquals("alarm", intentObject.domain)
    }
}
