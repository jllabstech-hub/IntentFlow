package com.intentflow.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ReasoningConstraint(
    val constraintId: String,
    val description: String,
    val isMandatory: Boolean = true
)

@Serializable
data class AlternativePlanPath(
    val pathId: String,
    val explanation: String,
    val estimatedLatencyMs: Long,
    val alternativeGraph: IntentGraph
)

@Serializable
data class OptimizationResult(
    val originalGraph: IntentGraph,
    val optimizedGraph: IntentGraph,
    val alternativePaths: List<AlternativePlanPath> = emptyList(),
    val isOptimized: Boolean = false,
    val optimizationNotes: String? = null
)
