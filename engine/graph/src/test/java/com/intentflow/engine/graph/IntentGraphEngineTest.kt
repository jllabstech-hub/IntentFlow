package com.intentflow.engine.graph

import com.intentflow.core.model.IntentDefinition
import com.intentflow.engine.search.MatchType
import com.intentflow.engine.search.ScoredIntentMatch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class IntentGraphEngineTest {

    private val validator = GraphValidator()
    private lateinit var graphEngine: IntentGraphEngine

    private val intentA = IntentDefinition("travel.plan", "Plan Vacation", "Desc", "travel")
    private val intentB = IntentDefinition("flight.book", "Book Flight", "Desc", "flight")

    @Before
    fun setup() {
        graphEngine = DefaultIntentGraphEngine(validator)
    }

    @Test
    fun testBuildSingleIntentGraph() = runBlocking {
        val matches = listOf(ScoredIntentMatch(intentA, score = 0.95f, matchType = MatchType.EXACT))
        val graph = graphEngine.buildGraph("Plan my vacation", matches, null)

        assertNotNull(graph)
        assertEquals(1, graph.nodes.size)
        assertEquals(0, graph.edges.size)
        assertTrue(graphEngine.validateGraph(graph))
    }

    @Test
    fun testBuildCompositeMultiStepIntentGraph() = runBlocking {
        val matches = listOf(
            ScoredIntentMatch(intentA, score = 0.85f, matchType = MatchType.PREFIX),
            ScoredIntentMatch(intentB, score = 0.80f, matchType = MatchType.FUZZY)
        )
        val graph = graphEngine.buildGraph("Plan my vacation and book flight", matches, null)

        assertNotNull(graph)
        assertEquals(2, graph.nodes.size)
        assertEquals(1, graph.edges.size)
        assertTrue(graphEngine.validateGraph(graph))
    }
}
