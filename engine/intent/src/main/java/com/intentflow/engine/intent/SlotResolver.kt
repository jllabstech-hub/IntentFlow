package com.intentflow.engine.intent

import com.intentflow.core.model.ContextObject
import com.intentflow.core.model.SlotDefinition
import com.intentflow.core.model.SlotMetadata
import com.intentflow.core.model.SlotType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Metadata-driven Slot Resolver.
 * Validates, normalizes raw values, retrieves suggestions, and constructs complete SlotMetadata descriptors.
 */
@Singleton
class SlotResolver @Inject constructor(
    private val validator: SlotValidator,
    private val suggestionEngine: SlotSuggestionEngine
) {

    fun resolveSlot(
        slot: SlotDefinition,
        rawValue: String?,
        context: ContextObject? = null
    ): SlotMetadata {
        val effectiveRawValue = rawValue ?: slot.defaultValue
        val validationResult = validator.validateSlot(slot, effectiveRawValue)
        val normalized = normalizeValue(slot.slotType, effectiveRawValue)
        val suggestions = suggestionEngine.generateSuggestions(slot, context)

        return SlotMetadata(
            slotName = slot.slotName,
            displayName = slot.displayName,
            slotType = slot.slotType,
            pickerType = slot.pickerType,
            isRequired = slot.required,
            defaultValue = slot.defaultValue,
            currentRawValue = effectiveRawValue,
            normalizedValue = normalized,
            validationResult = validationResult,
            suggestions = suggestions,
            pickerMetadata = buildPickerMetadata(slot)
        )
    }

    private fun normalizeValue(slotType: SlotType, rawValue: String?): String? {
        if (rawValue.isNullOrBlank()) return null

        val trimmed = rawValue.trim()
        return when (slotType) {
            SlotType.BOOLEAN -> {
                val lower = trimmed.lowercase()
                if (lower in listOf("true", "yes", "1", "on")) "true" else "false"
            }
            SlotType.DATE -> normalizeDate(trimmed)
            SlotType.TIME -> normalizeTime(trimmed)
            SlotType.NUMBER -> {
                try {
                    trimmed.toDouble().toString()
                } catch (e: Exception) {
                    trimmed
                }
            }
            else -> trimmed
        }
    }

    private fun normalizeDate(value: String): String {
        val lower = value.lowercase()
        val cal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return when (lower) {
            "today" -> dateFormat.format(cal.time)
            "tomorrow" -> {
                cal.add(Calendar.DAY_OF_YEAR, 1)
                dateFormat.format(cal.time)
            }
            "yesterday" -> {
                cal.add(Calendar.DAY_OF_YEAR, -1)
                dateFormat.format(cal.time)
            }
            else -> value
        }
    }

    private fun normalizeTime(value: String): String {
        val lower = value.lowercase().trim()
        if (lower.contains("pm") && !lower.contains(":")) {
            val hour = lower.replace("pm", "").trim().toIntOrNull()
            if (hour != null && hour < 12) {
                return String.format(Locale.getDefault(), "%02d:00", hour + 12)
            }
        } else if (lower.contains("am") && !lower.contains(":")) {
            val hour = lower.replace("am", "").trim().toIntOrNull()
            if (hour != null) {
                return String.format(Locale.getDefault(), "%02d:00", hour)
            }
        }
        return value
    }

    private fun buildPickerMetadata(slot: SlotDefinition): Map<String, String> {
        val metadata = mutableMapOf<String, String>()
        metadata["pickerType"] = slot.pickerType.name
        metadata["slotType"] = slot.slotType.name
        if (!slot.validationRegex.isNullOrBlank()) {
            metadata["regex"] = slot.validationRegex!!
        }
        return metadata
    }
}
