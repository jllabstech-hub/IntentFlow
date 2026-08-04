package com.intentflow.engine.planner

import com.intentflow.core.model.DependencyType
import com.intentflow.core.model.IntentGraph
import com.intentflow.core.model.IntentGraphEdge
import com.intentflow.core.model.IntentGraphNode
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class IntentPlannerTest {

    private lateinit var planner: IntentPlanner

    @Before
    fun setup() {
        planner = DefaultIntentPlanner()
    }

    @Test
    fun testPlanGraphTraversesNodesInTopologicalOrder() = runBlocking {
        val rootNode = IntentGraphNode(nodeId = "node_1", intentId = "travel.plan", domain = "travel", isRoot = true)
        val childNode = IntentGraphNode(nodeId = "node_2", intentId = "flight.book", domain = "flight")

        val graph = IntentGraph(
            graphId = "g1",
            rootNodeId = "node_1",
            rawQuery = "Plan vacation and book flight",
            nodes = mapOf("node_1" to rootNode, "node_2" to childNode),
            edges = listOf(IntentGraphEdge("node_1", "node_2", DependencyType.HARD_DEPENDENCY))
        )

        val plannedIntents = planner.planGraph(graph)

        assertNotNull(plannedIntents)
        assertEquals(2, plannedIntents.size)
        assertEquals("travel.plan", plannedIntents[0].intentId)
        assertEquals("flight.book", plannedIntents[1].intentId)
    }
}
