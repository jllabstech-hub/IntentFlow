package com.intentflow.engine.search

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FuzzyMatcherTest {

    @Test
    fun testLevenshteinSimilarityExactMatch() {
        val sim = FuzzyMatcher.calculateLevenshteinSimilarity("Send Message", "Send Message")
        assertEquals(1.0f, sim, 0.001f)
    }

    @Test
    fun testLevenshteinSimilarityPartialDifference() {
        val sim = FuzzyMatcher.calculateLevenshteinSimilarity("Send Message", "Send Text")
        assertTrue("Similarity should be around 0.5-0.7", sim in 0.4f..0.8f)
    }

    @Test
    fun testTokenJaccardSimilarityExactOverlap() {
        val sim = FuzzyMatcher.calculateTokenJaccardSimilarity("send message to mom", "send message to dad")
        // 3 shared tokens ("send", "message", "to") out of 5 unique total tokens
        assertEquals(3.0f / 5.0f, sim, 0.01f)
    }

    @Test
    fun testTokenJaccardSimilarityNoOverlap() {
        val sim = FuzzyMatcher.calculateTokenJaccardSimilarity("set alarm", "play music")
        assertEquals(0.0f, sim, 0.001f)
    }
}
