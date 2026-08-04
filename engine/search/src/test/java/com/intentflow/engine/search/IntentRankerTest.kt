package com.intentflow.engine.search

import com.intentflow.core.model.IntentDefinition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IntentRankerTest {

    private val ranker = IntentRanker()

    private val testIntent = IntentDefinition(
        intentId = "messaging.send",
        name = "Send Message",
        description = "Sends SMS",
        domain = "messaging",
        examples = listOf("Send message to Alice")
    )

    @Test
    fun testExactMatchRanking() {
        val match = ranker.rankMatch("Send message to Alice", testIntent, "Send message to Alice")
        assertEquals(MatchType.EXACT, match.matchType)
        assertEquals(1.0f, match.score, 0.001f)
    }

    @Test
    fun testPrefixMatchRanking() {
        val match = ranker.rankMatch("Send message", testIntent, "Send message to Alice")
        assertEquals(MatchType.PREFIX, match.matchType)
        assertTrue(match.score in 0.65f..0.95f)
    }

    @Test
    fun testContainsMatchRanking() {
        val match = ranker.rankMatch("xyz", testIntent, "abc xyz def")
        assertEquals(MatchType.CONTAINS, match.matchType)
        assertEquals(0.60f, match.score, 0.01f)
    }

    @Test
    fun testMergeAndRankSelectsHighestScorePerIntent() {
        val intent1 = IntentDefinition("alarm.set", "Set Alarm", "Desc", "alarm")
        val intent2 = IntentDefinition("phone.call", "Call Contact", "Desc", "phone")

        val matches = listOf(
            ScoredIntentMatch(intent1, score = 0.50f, matchType = MatchType.FUZZY),
            ScoredIntentMatch(intent1, score = 0.90f, matchType = MatchType.PREFIX),
            ScoredIntentMatch(intent2, score = 0.80f, matchType = MatchType.PREFIX)
        )

        val merged = ranker.mergeAndRankIntents(matches, limit = 10)
        assertEquals(2, merged.size)
        assertEquals("alarm.set", merged.first().intent.intentId)
        assertEquals(0.90f, merged.first().score, 0.001f)
    }
}
