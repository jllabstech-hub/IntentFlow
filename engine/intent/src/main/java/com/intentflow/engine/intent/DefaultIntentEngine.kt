package com.intentflow.engine.intent

import com.intentflow.catalog.api.KnowledgeCatalogRepository
import com.intentflow.core.model.ContextSnapshot
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.IntentState
import com.intentflow.core.model.SlotValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultIntentEngine @Inject constructor(
    private val catalogRepository: KnowledgeCatalogRepository
) : IntentEngine {

    private val _currentState = MutableStateFlow<IntentState>(IntentState.Idle)
    override val currentState: StateFlow<IntentState> = _currentState.asStateFlow()

    override suspend fun processInput(naturalLanguageInput: String, context: ContextSnapshot?): IntentObject {
        _currentState.value = IntentState.ProcessingInput(naturalLanguageInput)

        // Offline intent rule-matching lookup foundation
        val matches = catalogRepository.searchUtterances(naturalLanguageInput, limit = 1)
        val matchedIntent = matches.firstOrNull()

        val intentObject = IntentObject(
            id = UUID.randomUUID().toString(),
            intentId = matchedIntent?.intentId ?: "unknown.fallback",
            domain = matchedIntent?.domain ?: "general",
            slots = emptyMap(),
            missingSlots = matchedIntent?.requiredSlots ?: emptyList(),
            confidence = if (matchedIntent != null) 0.85f else 0.20f,
            context = context?.toContextObject()
        )

        if (intentObject.isComplete) {
            _currentState.value = IntentState.IntentIdentified(intentObject)
        } else {
            _currentState.value = IntentState.SlotFilling(intentObject)
        }

        return intentObject
    }

    override suspend fun updateSlot(intentObject: IntentObject, slotName: String, slotValue: String): IntentObject {
        val updatedSlots = intentObject.slots.toMutableMap()
        updatedSlots[slotName] = SlotValue(rawValue = slotValue, displayValue = slotValue)

        val remainingMissing = intentObject.missingSlots.filter { it.slotName != slotName }
        val updatedIntent = intentObject.copy(
            slots = updatedSlots,
            missingSlots = remainingMissing
        )

        if (updatedIntent.isComplete) {
            _currentState.value = IntentState.IntentIdentified(updatedIntent)
        } else {
            _currentState.value = IntentState.SlotFilling(updatedIntent)
        }

        return updatedIntent
    }

    override fun resetState() {
        _currentState.value = IntentState.Idle
    }
}
