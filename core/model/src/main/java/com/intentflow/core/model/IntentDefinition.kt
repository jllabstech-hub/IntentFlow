package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Static metadata describing an intent for catalog indexing and search.
 * Fully immutable and serializable via Kotlin Serialization.
 */
@Serializable
data class IntentDefinition(
    val intentId: String,
    val name: String,
    val description: String,
    val domain: String,
    val examples: List<String> = emptyList(),
    val version: Int = 1
) {
    val requiredSlots: List<SlotDefinition> get() = emptyList()
    val optionalSlots: List<SlotDefinition> get() = emptyList()

    /** Validates required fields on this intent definition. */
    fun validate(): ValidationResult {
        if (intentId.isBlank()) {
            return ValidationResult.Invalid("intentId cannot be blank")
        }
        if (name.isBlank()) {
            return ValidationResult.Invalid("name cannot be blank for intent '$intentId'")
        }
        if (domain.isBlank()) {
            return ValidationResult.Invalid("domain cannot be blank for intent '$intentId'")
        }
        return ValidationResult.Valid
    }
}

/**
 * Executable behavioral contract for an intent.
 * Describes slots, validation rules, dependencies, default values, dynamic UI rules,
 * context rules, execution rules, and permissions.
 */
@Serializable
data class IntentSchema(
    val intentId: String,
    val slots: List<SlotDefinition> = emptyList(),
    val validationRules: Map<String, String> = emptyMap(),
    val dependencies: List<String> = emptyList(),
    val defaultValues: Map<String, String> = emptyMap(),
    val dynamicUiRules: Map<String, String> = emptyMap(),
    val contextRules: Map<String, String> = emptyMap(),
    val executionRules: Map<String, String> = emptyMap(),
    val permissionRequirements: List<String> = emptyList()
) {
    val requiredSlots: List<SlotDefinition> get() = slots.filter { it.required }
    val optionalSlots: List<SlotDefinition> get() = slots.filter { !it.required }
}
