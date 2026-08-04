package com.intentflow.engine.intent

import com.intentflow.catalog.api.CatalogRepository
import com.intentflow.core.model.CatalogData
import com.intentflow.core.model.IntentDefinition
import com.intentflow.core.model.IntentState
import com.intentflow.engine.graph.IntentGraphEngine
import com.intentflow.engine.intent.learning.LearningEngine
import com.intentflow.engine.planner.IntentPlanner
import com.intentflow.engine.search.IntentSearchRepository
import com.intentflow.engine.search.MatchType
import com.intentflow.engine.search.ScoredIntentMatch
import com.intentflow.engine.understanding.RuleBasedIntentUnderstandingEngine
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

class RuleBasedIntentEngineTest {

    private val catalogRepository: CatalogRepository = mockk()
    private val searchRepository: IntentSearchRepository = mockk()
    private val slotExtractor = SlotExtractor()
    private val confidenceScorer = ConfidenceScorer()
    private val learningEngine: LearningEngine = mockk(relaxed = true)
    private val graphEngine: IntentGraphEngine = mockk(relaxed = true)
    private val intentPlanner: IntentPlanner = mockk(relaxed = true)

    private lateinit var intentEngine: RuleBasedIntentUnderstandingEngine

    private val alarmIntent = IntentDefinition(
        intentId = "alarm.set",
        name = "Set Alarm",
        description = "Sets wake up alarm",
        domain = "alarm"
    )

    @Before
    fun setup() {
        every { catalogRepository.activeCatalogData } returns MutableStateFlow(CatalogData(version = mockk(relaxed = true)))
        coEvery { catalogRepository.getIntentById("alarm.set") } returns alarmIntent
        coEvery { searchRepository.searchIntents(any(), any()) } returns listOf(
            ScoredIntentMatch(alarmIntent, score = 0.90f, matchType = MatchType.EXACT)
        )

        intentEngine = RuleBasedIntentUnderstandingEngine(
            catalogRepository, searchRepository, slotExtractor,
            confidenceScorer, learningEngine, graphEngine, intentPlanner
        )
    }

    @Test
    fun testProcessInputReturnsIntentObjectWithExtractedSlots() = runBlocking {
        val result = intentEngine.understandInput("Set alarm for 7 AM")

        assertNotNull(result)
        assertEquals("alarm.set", result.intentId)
        assertEquals("alarm", result.domain)

        assertTrue(intentEngine.currentState.value is IntentState.IntentIdentified || intentEngine.currentState.value is IntentState.SlotFilling)
    }

    @Test
    fun testUpdateSlotFillsMissingSlot() = runBlocking {
        val initialObj = intentEngine.understandInput("Set alarm")
        val updatedObj = intentEngine.updateSlot(initialObj, "time", "07:00")

        assertNotNull(updatedObj.slots["time"])
        assertEquals("07:00", updatedObj.slots["time"]?.rawValue)
        assertTrue(updatedObj.missingSlots.isEmpty())
    }
}
