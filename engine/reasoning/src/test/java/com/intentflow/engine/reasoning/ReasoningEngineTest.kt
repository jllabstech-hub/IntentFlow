package com.intentflow.engine.reasoning

import com.intentflow.core.model.IntentGraph
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class ReasoningEngineTest {

    private lateinit var reasoningEngine: ReasoningEngine

    @Before
    fun setup() {
        reasoningEngine = NoOpReasoningEngine()
    }

    @Test
    fun testNoOpReasoningEngineDisabledByDefault() {
        assertFalse(reasoningEngine.isReasoningEnabled)
    }

    @Test
    fun testOptimizePlanReturnsOriginalGraphUnmodified() = runBlocking {
        val graph = IntentGraph(graphId = "graph_1", rootNodeId = "node_1", rawQuery = "test query", nodes = emptyMap(), edges = emptyList())
        val result = reasoningEngine.optimizePlan(graph)

        assertNotNull(result)
        assertFalse(result.isOptimized)
        assertEquals(graph, result.optimizedGraph)
    }
}
