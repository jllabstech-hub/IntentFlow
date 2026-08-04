package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Dependency relationship type between IntentGraph nodes.
 */
@Serializable
enum class DependencyType {
    HARD_DEPENDENCY,
    SOFT_DEPENDENCY,
    DATA_DEPENDENCY
}

/**
 * Edge representing dependency and data flow between IntentGraph nodes.
 */
@Serializable
data class IntentGraphEdge(
    val sourceNodeId: String,
    val targetNodeId: String,
    val dependencyType: DependencyType = DependencyType.HARD_DEPENDENCY,
    val slotDataMapping: Map<String, String> = emptyMap()
)

/**
 * Node within an IntentGraph representing an individual intent operation or composite sub-goal.
 * Uses [ExecutionMode] from [ExecutionEngineModels] — no redeclaration here.
 */
@Serializable
data class IntentGraphNode(
    val nodeId: String,
    val intentId: String,
    val domain: String,
    val isRoot: Boolean = false,
    val isOptional: Boolean = false,
    val executionMode: ExecutionMode = ExecutionMode.SEQUENTIAL,
    val confidence: Float = 1.0f,
    val slots: Map<String, SlotValue> = emptyMap(),
    val missingSlots: List<SlotDefinition> = emptyList(),
    val metadata: Map<String, String> = emptyMap()
)

/**
 * Directed Acyclic Graph (DAG) representing multi-step intent execution plans.
 */
@Serializable
data class IntentGraph(
    val graphId: String,
    val rootNodeId: String,
    val rawQuery: String,
    val nodes: Map<String, IntentGraphNode>,
    val edges: List<IntentGraphEdge>,
    val overallConfidence: Float = 1.0f,
    val createdAt: Long = System.currentTimeMillis()
)
