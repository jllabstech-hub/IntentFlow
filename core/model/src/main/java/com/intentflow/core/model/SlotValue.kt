package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Tracks origin of a slot value.
 */
@Serializable
enum class SlotSource {
    USER_TEXT,
    CONTEXT_DEFAULT,
    AI_INFERRED,
    UI_PICKER,
    PREVIOUS_PREFERENCE
}

/**
 * Encapsulates a slot value with type-safe metadata, provenance, and confidence score.
 */
@Serializable
data class SlotValue(
    val rawValue: String? = null,
    val displayValue: String? = null,
    val source: SlotSource = SlotSource.USER_TEXT,
    val confidence: Float = 1.0f,
    val validationResult: ValidationResult = ValidationResult.Unchecked
)
