package com.intentflow.engine.intent

import com.intentflow.core.model.ContextObject
import com.intentflow.core.model.SlotDefinition
import com.intentflow.core.model.SlotType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Metadata-driven Slot Suggestion Engine.
 * Provides contextual suggestions for pickers based on SlotType, slot definitions, and context signals.
 */
@Singleton
class SlotSuggestionEngine @Inject constructor() {

    fun generateSuggestions(slot: SlotDefinition, context: ContextObject? = null): List<String> {
        val staticSuggestions = slot.suggestions

        if (staticSuggestions.isNotEmpty()) {
            return staticSuggestions
        }

        return when (slot.slotType) {
            SlotType.DATE -> listOf("Today", "Tomorrow", "This Weekend", "Next Monday")
            SlotType.TIME -> listOf("09:00", "12:00", "15:00", "18:00", "20:00")
            SlotType.BOOLEAN -> listOf("true", "false")
            SlotType.CONTACT -> context?.recentContacts?.take(5) ?: listOf("Mom", "Dad", "Alex", "Sarah")
            SlotType.LOCATION -> context?.locationName?.let { listOf(it, "Home", "Work") } ?: listOf("Home", "Work", "Current Location")
            SlotType.CURRENCY -> listOf("USD", "EUR", "GBP", "INR")
            SlotType.NUMBER -> listOf("1", "5", "10", "15", "30")
            else -> slot.exampleValues.ifEmpty { emptyList() }
        }
    }
}
