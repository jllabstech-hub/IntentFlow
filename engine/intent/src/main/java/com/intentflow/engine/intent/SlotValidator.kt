package com.intentflow.engine.intent

import com.intentflow.core.model.SlotDefinition
import com.intentflow.core.model.SlotType
import com.intentflow.core.model.ValidationResult
import java.util.regex.PatternSyntaxException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Metadata-driven Slot Validator.
 * Performs type-specific validation for all supported slot types in IntentFlow.
 */
@Singleton
class SlotValidator @Inject constructor() {

    fun validateSlot(slot: SlotDefinition, rawValue: String?): ValidationResult {
        if (rawValue.isNullOrBlank()) {
            return if (slot.required) {
                ValidationResult.Invalid("Slot '${slot.displayName}' is required")
            } else {
                ValidationResult.Valid
            }
        }

        val trimmed = rawValue.trim()

        // 1. Custom Regex validation if defined
        val patternString = slot.validationRegex
        if (!patternString.isNullOrBlank()) {
            try {
                val regex = Regex(patternString)
                if (!regex.matches(trimmed)) {
                    return ValidationResult.Invalid("Value does not match required format for '${slot.displayName}'")
                }
            } catch (e: PatternSyntaxException) {
                // Ignore regex error and fall back to type check
            }
        }

        // 2. Type-specific validation for all SlotTypes
        return when (slot.slotType) {
            SlotType.CONTACT -> validateContact(trimmed)
            SlotType.DATE -> validateDate(trimmed)
            SlotType.TIME -> validateTime(trimmed)
            SlotType.LOCATION -> validateLocation(trimmed)
            SlotType.CURRENCY -> validateCurrency(trimmed)
            SlotType.AMOUNT -> validateNumber(trimmed)
            SlotType.IMAGE -> validateImage(trimmed)
            SlotType.VIDEO -> validateFile(trimmed)
            SlotType.FILE -> validateFile(trimmed)
            SlotType.TEXT -> validateText(trimmed)
            SlotType.BOOLEAN -> validateBoolean(trimmed)
            SlotType.NUMBER -> validateNumber(trimmed)
            SlotType.ENUM -> validateEnum(trimmed, slot.suggestions)
            SlotType.MULTI_SELECT -> validateMultiSelect(trimmed, slot.suggestions)
            SlotType.EMAIL -> validateText(trimmed)
            SlotType.PHONE -> validateText(trimmed)
            SlotType.URL -> validateText(trimmed)
        }
    }

    private fun validateContact(value: String): ValidationResult {
        if (value.length < 2) return ValidationResult.Invalid("Contact name or number is too short")
        return ValidationResult.Valid
    }

    private fun validateDate(value: String): ValidationResult {
        val lower = value.lowercase()
        if (lower in listOf("today", "tomorrow", "yesterday", "tonight", "this weekend", "next monday", "next week")) {
            return ValidationResult.Valid
        }
        val isoRegex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
        val shortDateRegex = Regex("^\\d{1,2}/\\d{1,2}(/\\d{2,4})?$")
        if (isoRegex.matches(value) || shortDateRegex.matches(value)) {
            return ValidationResult.Valid
        }
        return ValidationResult.Invalid("Invalid date format. Use YYYY-MM-DD or relative date (e.g. today, tomorrow)")
    }

    private fun validateTime(value: String): ValidationResult {
        val timeRegex = Regex("^([0-1]?[0-9]|2[0-3]):[0-5][0-9](\\s?(AM|PM|am|pm))?$")
        val amPmSimpleRegex = Regex("^(1[0-2]|[1-9])\\s?(AM|PM|am|pm)$")
        if (timeRegex.matches(value) || amPmSimpleRegex.matches(value)) {
            return ValidationResult.Valid
        }
        return ValidationResult.Invalid("Invalid time format. Use HH:MM or 7 AM")
    }

    private fun validateLocation(value: String): ValidationResult {
        val coordsRegex = Regex("^-?\\d+(\\.\\d+)?,\\s*-?\\d+(\\.\\d+)?$")
        if (coordsRegex.matches(value) || value.length >= 2) {
            return ValidationResult.Valid
        }
        return ValidationResult.Invalid("Invalid location value")
    }

    private fun validateCurrency(value: String): ValidationResult {
        val currencyRegex = Regex("^[$€£¥]?\\s*\\d+(\\.\\d{1,2})?\\s*([A-Z]{3})?$")
        if (currencyRegex.matches(value)) {
            return ValidationResult.Valid
        }
        return ValidationResult.Invalid("Invalid currency value")
    }

    private fun validateImage(value: String): ValidationResult {
        val lower = value.lowercase()
        if (lower.startsWith("content://") || lower.startsWith("file://") ||
            lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".webp")
        ) {
            return ValidationResult.Valid
        }
        return ValidationResult.Invalid("Invalid image path or URI")
    }

    private fun validateFile(value: String): ValidationResult {
        if (value.startsWith("/") || value.startsWith("content://") || value.startsWith("file://") || value.contains(".")) {
            return ValidationResult.Valid
        }
        return ValidationResult.Invalid("Invalid file path or URI")
    }

    private fun validateText(value: String): ValidationResult {
        if (value.isNotBlank()) return ValidationResult.Valid
        return ValidationResult.Invalid("Text cannot be blank")
    }

    private fun validateBoolean(value: String): ValidationResult {
        val lower = value.lowercase()
        if (lower in listOf("true", "false", "yes", "no", "1", "0", "on", "off")) {
            return ValidationResult.Valid
        }
        return ValidationResult.Invalid("Value must be true/false or yes/no")
    }

    private fun validateNumber(value: String): ValidationResult {
        val numberRegex = Regex("^-?\\d+(\\.\\d+)?$")
        if (numberRegex.matches(value)) {
            return ValidationResult.Valid
        }
        return ValidationResult.Invalid("Value must be a valid number")
    }

    private fun validateEnum(value: String, allowedValues: List<String>): ValidationResult {
        if (allowedValues.isEmpty()) return ValidationResult.Valid
        if (allowedValues.any { it.equals(value, ignoreCase = true) }) {
            return ValidationResult.Valid
        }
        return ValidationResult.Invalid("Value '$value' is not one of allowed options: ${allowedValues.joinToString()}")
    }

    private fun validateMultiSelect(value: String, allowedValues: List<String>): ValidationResult {
        if (allowedValues.isEmpty()) return ValidationResult.Valid
        val selectedItems = value.split(",").map { it.trim() }
        val invalidItems = selectedItems.filter { item ->
            allowedValues.none { allowed -> allowed.equals(item, ignoreCase = true) }
        }
        if (invalidItems.isEmpty()) {
            return ValidationResult.Valid
        }
        return ValidationResult.Invalid("Invalid selection(s): ${invalidItems.joinToString()}")
    }
}
