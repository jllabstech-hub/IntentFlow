package com.intentflow.engine.graph

import com.intentflow.core.model.ContextSnapshot
import com.intentflow.core.model.DependencyType
import com.intentflow.core.model.ExecutionMode
import com.intentflow.core.model.IntentGraph
import com.intentflow.core.model.IntentGraphEdge
import com.intentflow.core.model.IntentGraphNode
import com.intentflow.engine.search.ScoredIntentMatch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

interface IntentGraphEngine {
    suspend fun buildGraph(
        rawQuery: String,
        candidateMatches: List<ScoredIntentMatch>,
        context: ContextSnapshot?
    ): IntentGraph

    fun validateGraph(graph: IntentGraph): Boolean
}

@Singleton
class GraphValidator @Inject constructor() {
    fun hasCycles(graph: IntentGraph): Boolean {
        val visited = mutableSetOf<String>()
        val recursionStack = mutableSetOf<String>()

        fun dfs(nodeId: String): Boolean {
            visited.add(nodeId)
            recursionStack.add(nodeId)

            val outgoingEdges = graph.edges.filter { it.sourceNodeId == nodeId }
            for (edge in outgoingEdges) {
                val neighbor = edge.targetNodeId
                if (!visited.contains(neighbor)) {
                    if (dfs(neighbor)) return true
                } else if (recursionStack.contains(neighbor)) {
                    return true
                }
            }

            recursionStack.remove(nodeId)
            return false
        }

        for (nodeId in graph.nodes.keys) {
            if (!visited.contains(nodeId)) {
                if (dfs(nodeId)) return true
            }
        }
        return false
    }
}

@Singleton
class DefaultIntentGraphEngine @Inject constructor(
    private val validator: GraphValidator
) : IntentGraphEngine {

    override suspend fun buildGraph(
        rawQuery: String,
        candidateMatches: List<ScoredIntentMatch>,
        context: ContextSnapshot?
    ): IntentGraph {
        val graphId = UUID.randomUUID().toString()

        if (candidateMatches.isEmpty()) {
            val rootNode = IntentGraphNode(
                nodeId = "root_fallback",
                intentId = "unknown.fallback",
                domain = "general",
                isRoot = true,
                confidence = 0.0f
            )
            return IntentGraph(
                graphId = graphId,
                rootNodeId = rootNode.nodeId,
                rawQuery = rawQuery,
                nodes = mapOf(rootNode.nodeId to rootNode),
                edges = emptyList(),
                overallConfidence = 0.0f
            )
        }

        val topMatch = candidateMatches.first()
        val isMultiStep = candidateMatches.size > 1 && topMatch.score < 0.90f && rawQuery.contains("and")

        if (!isMultiStep) {
            // Single-intent graph
            val rootNode = IntentGraphNode(
                nodeId = "node_0",
                intentId = topMatch.intent.intentId,
                domain = topMatch.intent.domain,
                isRoot = true,
                confidence = topMatch.score
            )
            return IntentGraph(
                graphId = graphId,
                rootNodeId = rootNode.nodeId,
                rawQuery = rawQuery,
                nodes = mapOf(rootNode.nodeId to rootNode),
                edges = emptyList(),
                overallConfidence = topMatch.score
            )
        } else {
            // Multi-intent composite graph (e.g. "Plan my vacation")
            val nodes = mutableMapOf<String, IntentGraphNode>()
            val edges = mutableListOf<IntentGraphEdge>()

            val rootNode = IntentGraphNode(
                nodeId = "root_${topMatch.intent.intentId}",
                intentId = topMatch.intent.intentId,
                domain = topMatch.intent.domain,
                isRoot = true,
                confidence = topMatch.score
            )
            nodes[rootNode.nodeId] = rootNode

            var parentNodeId = rootNode.nodeId
            candidateMatches.drop(1).take(3).forEachIndexed { index, childMatch ->
                val childNode = IntentGraphNode(
                    nodeId = "child_${index + 1}_${childMatch.intent.intentId}",
                    intentId = childMatch.intent.intentId,
                    domain = childMatch.intent.domain,
                    isRoot = false,
                    isOptional = index == 2,
                    executionMode = if (index % 2 == 0) ExecutionMode.SEQUENTIAL else ExecutionMode.PARALLEL,
                    confidence = childMatch.score
                )
                nodes[childNode.nodeId] = childNode

                edges.add(
                    IntentGraphEdge(
                        sourceNodeId = parentNodeId,
                        targetNodeId = childNode.nodeId,
                        dependencyType = if (index == 0) DependencyType.HARD_DEPENDENCY else DependencyType.DATA_DEPENDENCY
                    )
                )
                parentNodeId = childNode.nodeId
            }

            return IntentGraph(
                graphId = graphId,
                rootNodeId = rootNode.nodeId,
                rawQuery = rawQuery,
                nodes = nodes,
                edges = edges,
                overallConfidence = topMatch.score
            )
        }
    }

    override fun validateGraph(graph: IntentGraph): Boolean {
        return graph.nodes.isNotEmpty() && !validator.hasCycles(graph)
    }
}
