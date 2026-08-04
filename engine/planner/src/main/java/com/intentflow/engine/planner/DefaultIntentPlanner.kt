package com.intentflow.engine.planner

import com.intentflow.core.model.ExecutionPlan
import com.intentflow.core.model.ExecutionTarget
import com.intentflow.core.model.IntentGraph
import com.intentflow.core.model.IntentGraphNode
import com.intentflow.core.model.IntentObject
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

interface IntentPlanner {
    suspend fun planGraph(graph: IntentGraph): List<IntentObject>
    suspend fun buildCompositeExecutionPlan(graph: IntentGraph): ExecutionPlan
}

@Singleton
class DefaultIntentPlanner @Inject constructor() : IntentPlanner {

    override suspend fun planGraph(graph: IntentGraph): List<IntentObject> {
        val topologicalOrder = topologicalSort(graph)

        return topologicalOrder.map { node ->
            IntentObject(
                id = UUID.randomUUID().toString(),
                intentId = node.intentId,
                domain = node.domain,
                slots = node.slots,
                missingSlots = node.missingSlots,
                confidence = node.confidence,
                metadata = node.metadata + mapOf("graphNodeId" to node.nodeId, "graphId" to graph.graphId)
            )
        }
    }

    override suspend fun buildCompositeExecutionPlan(graph: IntentGraph): ExecutionPlan {
        val plannedIntents = planGraph(graph)
        val primary = plannedIntents.firstOrNull() ?: IntentObject(id = "1", intentId = "unknown.fallback", domain = "general")

        val target = when {
            primary.intentId.startsWith("plugin.") -> ExecutionTarget.ANDROID_PLUGIN
            primary.metadata["deepLink"] != null -> ExecutionTarget.SYSTEM_DEEP_LINK
            else -> ExecutionTarget.AI_PROVIDER
        }

        return ExecutionPlan(
            intentId = primary.intentId,
            target = target,
            targetHandlerId = primary.intentId,
            resolvedParameters = primary.slots.mapValues { it.value.rawValue ?: "" },
            requiresUserConfirmation = plannedIntents.size > 1
        )
    }

    private fun topologicalSort(graph: IntentGraph): List<IntentGraphNode> {
        val inDegree = mutableMapOf<String, Int>()
        graph.nodes.keys.forEach { inDegree[it] = 0 }

        graph.edges.forEach { edge ->
            inDegree[edge.targetNodeId] = (inDegree[edge.targetNodeId] ?: 0) + 1
        }

        val queue = ArrayDeque<String>()
        inDegree.filter { it.value == 0 }.keys.forEach { queue.add(it) }

        val sortedList = mutableListOf<IntentGraphNode>()
        while (queue.isNotEmpty()) {
            val currNodeId = queue.removeFirst()
            val node = graph.nodes[currNodeId]
            if (node != null) {
                sortedList.add(node)
            }

            graph.edges.filter { it.sourceNodeId == currNodeId }.forEach { edge ->
                val targetId = edge.targetNodeId
                inDegree[targetId] = (inDegree[targetId] ?: 1) - 1
                if (inDegree[targetId] == 0) {
                    queue.add(targetId)
                }
            }
        }

        return if (sortedList.size == graph.nodes.size) sortedList else graph.nodes.values.toList()
    }
}
