package com.intentflow.core.model

import kotlinx.serialization.Serializable

@Serializable
data class SkillInput(
    val inputKey: String,
    val dataType: String,
    val isRequired: Boolean = true,
    val defaultValue: String? = null
)

@Serializable
data class SkillOutput(
    val outputKey: String,
    val dataType: String,
    val description: String
)

@Serializable
data class SkillDependency(
    val dependencyId: String,
    val requiredCapability: String,
    val isOptional: Boolean = false
)

@Serializable
data class ExecutionStep(
    val stepId: String,
    val stepName: String,
    val targetCapabilityId: String,
    val inputMappings: Map<String, String> = emptyMap(),
    val outputMappings: Map<String, String> = emptyMap(),
    val stopOnFailure: Boolean = true
)

@Serializable
data class SkillFallback(
    val fallbackStepId: String? = null,
    val fallbackMessage: String = "Skill execution failed. Falling back to default provider.",
    val secondaryCapabilityId: String? = null
)

@Serializable
data class SkillDefinition(
    val skillId: String,
    val displayName: String,
    val description: String,
    val version: Int = 1,
    val inputs: List<SkillInput> = emptyList(),
    val outputs: List<SkillOutput> = emptyList(),
    val dependencies: List<SkillDependency> = emptyList(),
    val executionOrder: List<ExecutionStep> = emptyList(),
    val fallback: SkillFallback = SkillFallback(),
    val isAiGenerated: Boolean = false
)
