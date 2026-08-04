package com.intentflow.core.model

import kotlinx.serialization.Serializable
import java.util.regex.PatternSyntaxException

/**
 * Immutable definition schema for an Intent slot.
 * Fully serializable via Kotlin Serialization.
 */
@Serializable
data class SlotDefinition(
    val slotName: String,
    val displayName: String,
    val slotType: SlotType,
    val intentId: String = "",
    val required: Boolean = false,
    val defaultValue: String? = null,
    val validationRegex: String? = null,
    val pickerType: PickerType = PickerType.TEXT_INPUT,
    val suggestions: List<String> = emptyList(),
    val aliases: List<String> = emptyList(),
    val exampleValues: List<String> = emptyList()
) {
    /**
     * Validates SlotDefinition fields and compiles validation regex if provided.
     */
    fun validate(): ValidationResult {
        if (slotName.isBlank()) {
            return ValidationResult.Invalid("slotName cannot be blank")
        }
        if (displayName.isBlank()) {
            return ValidationResult.Invalid("displayName cannot be blank for slot '$slotName'")
        }
        if (!validationRegex.isNullOrBlank()) {
            try {
                Regex(validationRegex)
            } catch (e: PatternSyntaxException) {
                return ValidationResult.Invalid("Invalid validationRegex for slot '$slotName': ${e.message}")
            }
        }
        return ValidationResult.Valid
    }
}
