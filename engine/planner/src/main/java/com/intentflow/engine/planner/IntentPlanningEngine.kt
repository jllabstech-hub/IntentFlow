package com.intentflow.engine.planner

import com.intentflow.core.model.ExecutionPlan
import com.intentflow.core.model.ExecutionTarget
import com.intentflow.core.model.IntentGraph
import com.intentflow.core.model.IntentGraphNode
import com.intentflow.core.model.IntentObject
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 2. Intent Planning Engine Interface.
 * Responsibilities: Build Execution Plan, Resolve Dependencies, Determine Execution Strategy.
 */
interface IntentPlanningEngine {
    suspend fun createPlan(intentObject: IntentObject): ExecutionPlan
    suspend fun createCompositePlan(graph: IntentGraph): ExecutionPlan
    fun resolveDependencies(graph: IntentGraph): List<IntentObject>
}

/**
 * Production-ready implementation of Intent Planning Engine.
 */
@Singleton
class DefaultIntentPlanningEngine @Inject constructor() : IntentPlanningEngine {

    override suspend fun createPlan(intentObject: IntentObject): ExecutionPlan {
        val mapping = intentObject.metadata["executionMapping"]
        val resolvedParams = intentObject.slots.mapValues { it.value.rawValue ?: "" }

        val target = when {
            mapping?.startsWith("plugin.") == true || intentObject.intentId.startsWith("plugin.") -> ExecutionTarget.ANDROID_PLUGIN
            intentObject.metadata["deepLink"]?.startsWith("intentflow://") == true -> ExecutionTarget.SYSTEM_DEEP_LINK
            else -> ExecutionTarget.AI_PROVIDER
        }

        val targetHandler = mapping ?: intentObject.metadata["deepLink"] ?: intentObject.intentId
        val requiresConfirmation = intentObject.domain in listOf("payments", "banking")

        return ExecutionPlan(
            intentId = intentObject.intentId,
            target = target,
            targetHandlerId = targetHandler,
            resolvedParameters = resolvedParams,
            requiresUserConfirmation = requiresConfirmation,
            confirmationMessage = if (requiresConfirmation) "Confirm action for ${intentObject.intentId}?" else null
        )
    }

    override suspend fun createCompositePlan(graph: IntentGraph): ExecutionPlan {
        val plannedIntents = resolveDependencies(graph)
        val primary = plannedIntents.firstOrNull() ?: IntentObject(id = "1", intentId = "unknown.fallback", domain = "general")
        return createPlan(primary).copy(requiresUserConfirmation = plannedIntents.size > 1)
    }

    override fun resolveDependencies(graph: IntentGraph): List<IntentObject> {
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
