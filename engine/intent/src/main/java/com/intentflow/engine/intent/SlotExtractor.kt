package com.intentflow.engine.intent

import com.intentflow.core.model.EntityDefinition
import com.intentflow.core.model.IntentDefinition
import com.intentflow.core.model.SlotDefinition
import com.intentflow.core.model.SlotSource
import com.intentflow.core.model.SlotType
import com.intentflow.core.model.SlotValue
import com.intentflow.core.model.ValidationResult
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result container for slot extraction.
 */
data class ExtractionResult(
    val extractedSlots: Map<String, SlotValue>,
    val missingRequiredSlots: List<SlotDefinition>
)

typealias SlotExtractionResult = ExtractionResult

/**
 * Offline, rule-based Slot Extractor.
 * Extracts slot values from natural language text using regex, entity dictionaries, and keyword positioning.
 */
@Singleton
class SlotExtractor @Inject constructor() {

    fun extractSlots(
        input: String,
        intent: IntentDefinition,
        entities: List<EntityDefinition> = emptyList()
    ): ExtractionResult {
        val extractedMap = mutableMapOf<String, SlotValue>()
        val allSlots = intent.requiredSlots + intent.optionalSlots
        val lowerInput = input.lowercase().trim()

        for (slot in allSlots) {
            val extractedValue = tryExtractSlotValue(lowerInput, input, slot, entities)
            if (extractedValue != null) {
                extractedMap[slot.slotName] = extractedValue
            }
        }

        val missingRequired = intent.requiredSlots.filter { requiredSlot ->
            !extractedMap.containsKey(requiredSlot.slotName)
        }

        return ExtractionResult(
            extractedSlots = extractedMap,
            missingRequiredSlots = missingRequired
        )
    }

    private fun tryExtractSlotValue(
        lowerInput: String,
        rawInput: String,
        slot: SlotDefinition,
        entities: List<EntityDefinition>
    ): SlotValue? {
        // 1. Custom Regex Validation check if provided
        val validationPattern = slot.validationRegex
        if (!validationPattern.isNullOrBlank()) {
            val regex = Regex(validationPattern)
            val match = regex.find(rawInput)
            if (match != null) {
                return SlotValue(
                    rawValue = match.value,
                    displayValue = match.value,
                    source = SlotSource.USER_TEXT,
                    confidence = 0.95f,
                    validationResult = ValidationResult.Valid
                )
            }
        }

        // 2. Type-specific Pattern Extraction
        when (slot.slotType) {
            SlotType.PHONE -> {
                val phonePattern = Pattern.compile("(\\+?\\d{1,3}[- .]?)?\\(?\\d{3}\\)?[- .]?\\d{3}[- .]?\\d{4}")
                val matcher = phonePattern.matcher(rawInput)
                if (matcher.find()) {
                    return SlotValue(matcher.group(), matcher.group(), SlotSource.USER_TEXT, 0.90f, ValidationResult.Valid)
                }
            }
            SlotType.EMAIL -> {
                val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
                val match = emailRegex.find(rawInput)
                if (match != null) {
                    return SlotValue(match.value, match.value, SlotSource.USER_TEXT, 0.95f, ValidationResult.Valid)
                }
            }
            SlotType.NUMBER -> {
                val numberRegex = Regex("\\b\\d+(\\.\\d+)?\\b")
                val match = numberRegex.find(rawInput)
                if (match != null) {
                    return SlotValue(match.value, match.value, SlotSource.USER_TEXT, 0.85f, ValidationResult.Valid)
                }
            }
            SlotType.BOOLEAN -> {
                if (lowerInput.contains("on") || lowerInput.contains("enable") || lowerInput.contains("yes") || lowerInput.contains("true")) {
                    return SlotValue("true", "Enabled", SlotSource.USER_TEXT, 0.90f, ValidationResult.Valid)
                } else if (lowerInput.contains("off") || lowerInput.contains("disable") || lowerInput.contains("no") || lowerInput.contains("false")) {
                    return SlotValue("false", "Disabled", SlotSource.USER_TEXT, 0.90f, ValidationResult.Valid)
                }
            }
            else -> {}
        }

        // 3. Keyword / Alias preposition extraction (e.g. "to Alice", "for 10 minutes", "labeled Work")
        for (alias in slot.aliases + listOf(slot.slotName, "to", "for", "with", "at", "about", "labeled", "titled")) {
            val aliasMarker = "$alias "
            if (lowerInput.contains(aliasMarker)) {
                val startIndex = lowerInput.indexOf(aliasMarker) + aliasMarker.length
                val substring = rawInput.substring(startIndex).trim()
                val stopWords = listOf(" at ", " for ", " to ", " with ", " on ", " about ", " in ")
                var extractedText = substring

                for (stopWord in stopWords) {
                    if (extractedText.lowercase().contains(stopWord)) {
                        extractedText = extractedText.substring(0, extractedText.lowercase().indexOf(stopWord)).trim()
                        break
                    }
                }

                if (extractedText.isNotBlank()) {
                    return SlotValue(
                        rawValue = extractedText,
                        displayValue = extractedText,
                        source = SlotSource.USER_TEXT,
                        confidence = 0.80f,
                        validationResult = ValidationResult.Valid
                    )
                }
            }
        }

        // 4. Entity dictionary matching
        for (entity in entities) {
            for (value in entity.values) {
                if (lowerInput.contains(value.lowercase())) {
                    return SlotValue(value, value, SlotSource.USER_TEXT, 0.85f, ValidationResult.Valid)
                }
            }
        }

        return null
    }
}
