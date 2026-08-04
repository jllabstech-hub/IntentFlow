package com.intentflow.engine.intent

import com.intentflow.core.model.ContextSnapshot
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.IntentState
import kotlinx.coroutines.flow.StateFlow

/**
 * Base Interface for the Intent Engine.
 * Responsible for intent detection, slot extraction, confidence scoring, and state transitions.
 * Does not depend on external AI models for core rule-based matching.
 */
interface IntentEngine {
    val currentState: StateFlow<IntentState>

    suspend fun processInput(naturalLanguageInput: String, context: ContextSnapshot? = null): IntentObject
    suspend fun updateSlot(intentObject: IntentObject, slotName: String, slotValue: String): IntentObject
    fun resetState()
}
