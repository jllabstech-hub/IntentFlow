package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Metadata-driven descriptor for a resolved or active slot.
 * Provides picker configuration, validation status, suggestions, and normalized values.
 */
@Serializable
data class SlotMetadata(
    val slotName: String,
    val displayName: String,
    val slotType: SlotType,
    val pickerType: PickerType,
    val isRequired: Boolean = false,
    val defaultValue: String? = null,
    val currentRawValue: String? = null,
    val normalizedValue: String? = null,
    val validationResult: ValidationResult = ValidationResult.Unchecked,
    val suggestions: List<String> = emptyList(),
    val pickerMetadata: Map<String, String> = emptyMap()
)
