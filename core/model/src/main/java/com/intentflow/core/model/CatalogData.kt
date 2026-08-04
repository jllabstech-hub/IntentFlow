package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * An example utterance tied to a specific intent in the Knowledge Catalog.
 * Utterances are used for NLU training, FTS5 indexing, and intent matching.
 */
@Serializable
data class UtteranceDefinition(
    val utteranceId: String,
    val intentId: String,
    val text: String,
    val language: String = "en",
    val metadata: Map<String, String> = emptyMap()
)

@Serializable
data class ExecutionRule(
    val ruleId: String,
    val intentId: String,
    val timeoutMs: Long = 5000,
    val maxRetries: Int = 3,
    val isIdempotent: Boolean = true,
    val requiresNetwork: Boolean = false
)

@Serializable
data class PermissionDefinition(
    val permissionId: String,
    val androidPermission: String,
    val rationale: String,
    val isMandatory: Boolean = true
)

@Serializable
data class ActionDefinition(
    val actionId: String,
    val intentId: String,
    val handlerId: String,
    val actionType: String
)

@Serializable
data class ValidationRule(
    val ruleId: String,
    val slotName: String,
    val pattern: String,
    val errorMessage: String
)

@Serializable
data class DependencyDefinition(
    val dependencyId: String,
    val sourceIntentId: String,
    val targetIntentId: String,
    val dependencyType: String
)

/**
 * Single Source of Truth Master Knowledge Catalog Data Structure.
 * Contains all 14 core catalog entities.
 * Versioned independently of the Android APK via OTA catalog delivery.
 */
@Serializable
data class CatalogData(
    val version: CatalogVersion,
    val domains: List<DomainDefinition> = emptyList(),
    val intents: List<IntentDefinition> = emptyList(),
    val slots: List<SlotDefinition> = emptyList(),
    val utterances: List<UtteranceDefinition> = emptyList(),
    val entities: List<EntityDefinition> = emptyList(),
    val capabilities: List<CapabilityDescriptor> = emptyList(),
    val intentSchemas: List<IntentSchema> = emptyList(),
    val executionRules: List<ExecutionRule> = emptyList(),
    val permissions: List<PermissionDefinition> = emptyList(),
    val uiSchemas: List<UISchema> = emptyList(),
    val actions: List<ActionDefinition> = emptyList(),
    val validationRules: List<ValidationRule> = emptyList(),
    val dependencies: List<DependencyDefinition> = emptyList()
)
