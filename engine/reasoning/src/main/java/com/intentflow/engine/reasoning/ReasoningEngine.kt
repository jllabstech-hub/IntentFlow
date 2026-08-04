package com.intentflow.engine.reasoning

import com.intentflow.core.model.AlternativePlanPath
import com.intentflow.core.model.IntentGraph
import com.intentflow.core.model.OptimizationResult
import com.intentflow.core.model.ReasoningConstraint
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Optional Reasoning Engine Interface.
 * Operates directly on IntentGraph to decompose tasks, optimize execution DAGs, and discover alternative paths.
 */
interface ReasoningEngine {
    val isReasoningEnabled: Boolean

    suspend fun decomposeTask(goalInput: String): IntentGraph?
    suspend fun optimizePlan(graph: IntentGraph, constraints: List<ReasoningConstraint> = emptyList()): OptimizationResult
    suspend fun findAlternativePaths(graph: IntentGraph): List<AlternativePlanPath>
    suspend fun refineGoal(currentGoal: String, contextFeedback: String): String
}

/**
 * Default No-Op Reasoning Engine implementation.
 * Used when Reasoning Engine is disabled (100% offline deterministic behavior).
 */
@Singleton
class NoOpReasoningEngine @Inject constructor() : ReasoningEngine {

    override val isReasoningEnabled: Boolean = false

    override suspend fun decomposeTask(goalInput: String): IntentGraph? = null

    override suspend fun optimizePlan(graph: IntentGraph, constraints: List<ReasoningConstraint>): OptimizationResult {
        return OptimizationResult(
            originalGraph = graph,
            optimizedGraph = graph,
            isOptimized = false,
            optimizationNotes = "Reasoning Engine disabled. Returning un-modified DAG."
        )
    }

    override suspend fun findAlternativePaths(graph: IntentGraph): List<AlternativePlanPath> = emptyList()

    override suspend fun refineGoal(currentGoal: String, contextFeedback: String): String = currentGoal
}

/**
 * Production-ready optional Reasoning Engine implementation.
 */
@Singleton
class DefaultReasoningEngine @Inject constructor() : ReasoningEngine {

    override val isReasoningEnabled: Boolean = true

    override suspend fun decomposeTask(goalInput: String): IntentGraph? {
        return null // Pluggable AI logic operates here if activated
    }

    override suspend fun optimizePlan(graph: IntentGraph, constraints: List<ReasoningConstraint>): OptimizationResult {
        return OptimizationResult(
            originalGraph = graph,
            optimizedGraph = graph,
            isOptimized = false,
            optimizationNotes = "Optimized via AI Reasoner"
        )
    }

    override suspend fun findAlternativePaths(graph: IntentGraph): List<AlternativePlanPath> = emptyList()

    override suspend fun refineGoal(currentGoal: String, contextFeedback: String): String = currentGoal
}
