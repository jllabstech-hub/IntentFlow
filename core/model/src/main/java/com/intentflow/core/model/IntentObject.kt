package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Universal Internal Language of IntentFlow.
 * Represents an enriched, structured intent ready for slot filling or execution.
 * Fully immutable and serializable via Kotlin Serialization.
 */
@Serializable
data class IntentObject(
    val id: String,
    val intentId: String,
    val domain: String,
    val slots: Map<String, SlotValue> = emptyMap(),
    val missingSlots: List<SlotDefinition> = emptyList(),
    val confidence: Float = 0.0f,
    val metadata: Map<String, String> = emptyMap(),
    val permissions: List<String> = emptyList(),
    val executionPlan: ExecutionPlan? = null,
    val context: ContextObject? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val version: String = "1.0"
) {
    val isComplete: Boolean
        get() = missingSlots.isEmpty() && confidence >= 0.70f

    /**
     * Validates IntentObject integrity.
     */
    fun validate(): ValidationResult {
        if (id.isBlank()) {
            return ValidationResult.Invalid("IntentObject id cannot be blank")
        }
        if (intentId.isBlank()) {
            return ValidationResult.Invalid("intentId cannot be blank")
        }
        if (domain.isBlank()) {
            return ValidationResult.Invalid("domain cannot be blank")
        }
        if (confidence !in 0.0f..1.0f) {
            return ValidationResult.Invalid("confidence must be between 0.0 and 1.0")
        }
        if (context != null) {
            val contextResult = context.validate()
            if (contextResult is ValidationResult.Invalid) {
                return ValidationResult.Invalid("Invalid context in IntentObject: ${contextResult.reason}")
            }
        }
        return ValidationResult.Valid
    }
}
