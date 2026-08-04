package com.intentflow.engine.intent

import com.intentflow.core.model.IntentDefinition
import com.intentflow.core.model.SlotValue
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Calculates overall confidence score for an IntentObject based on rule-matching strength and slot completion.
 */
@Singleton
class ConfidenceScorer @Inject constructor() {

    fun calculateConfidence(
        intentMatchScore: Float,
        intent: IntentDefinition,
        extractedSlots: Map<String, SlotValue>
    ): Float {
        val baseScore = intentMatchScore.coerceIn(0.0f, 1.0f)

        if (intent.requiredSlots.isEmpty()) {
            return baseScore
        }

        val requiredCount = intent.requiredSlots.size
        val filledRequiredCount = intent.requiredSlots.count { slot ->
            extractedSlots.containsKey(slot.slotName) && extractedSlots[slot.slotName]?.rawValue != null
        }

        val slotCompletionRatio = filledRequiredCount.toFloat() / requiredCount.toFloat()

        // Weighted combination: 60% Intent Match Score + 40% Slot Completion Ratio
        val finalScore = (baseScore * 0.60f) + (slotCompletionRatio * 0.40f)
        return finalScore.coerceIn(0.0f, 1.0f)
    }
}
