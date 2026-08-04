package com.intentflow.engine.intent.learning

import com.intentflow.core.model.IntentDefinition
import com.intentflow.engine.search.MatchType
import com.intentflow.engine.search.ScoredIntentMatch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LearningEngineTest {

    private val learningEngine = LearningEngine()

    private val intentA = IntentDefinition("messaging.send", "Send Message", "Desc", "messaging")
    private val intentB = IntentDefinition("phone.call", "Make Call", "Desc", "phone")

    @Before
    fun setup() {
        learningEngine.clearHistory()
    }

    @Test
    fun testPersonalizeRankingBoostsFrequentlyUsedIntent() {
        // Record 5 usages for intentA
        repeat(5) {
            learningEngine.recordIntentUsage("messaging.send", mapOf("recipient" to "Mom"))
        }

        val initialMatches = listOf(
            ScoredIntentMatch(intentB, score = 0.70f, matchType = MatchType.FUZZY),
            ScoredIntentMatch(intentA, score = 0.65f, matchType = MatchType.FUZZY)
        )

        val personalized = learningEngine.personalizeRanking(initialMatches)

        // intentA should now be ranked #1 due to local frequency boosting
        assertEquals("messaging.send", personalized.first().intent.intentId)
        assertTrue(personalized.first().score > 0.65f)
    }

    @Test
    fun testUserBehaviorHistoryTracking() {
        learningEngine.recordIntentUsage("alarm.set", mapOf("time" to "07:00"))
        val history = learningEngine.getUserBehaviorHistory()

        assertEquals(1, history.intentUsageCounts["alarm.set"])
        assertEquals("alarm.set", history.recentIntents.first())
        assertTrue(history.slotValueFrequency.containsKey("time"))
    }
}
