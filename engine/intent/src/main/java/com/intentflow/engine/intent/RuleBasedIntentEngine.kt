package com.intentflow.engine.intent

import com.intentflow.catalog.api.CatalogRepository
import com.intentflow.core.model.ContextObject
import com.intentflow.core.model.ContextSnapshot
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.IntentState
import com.intentflow.core.model.SlotValue
import com.intentflow.engine.graph.IntentGraphEngine
import com.intentflow.engine.intent.learning.LearningEngine
import com.intentflow.engine.planner.IntentPlanner
import com.intentflow.engine.search.IntentSearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production-ready offline Intent Engine implementation with IntentGraph DAG architecture.
 *
 * Pipeline Flow:
 * Natural Language → Intent Search → Intent Understanding → IntentGraph → IntentPlanner → IntentObject → Execution
 */
@Singleton
class RuleBasedIntentEngine @Inject constructor(
    private val catalogRepository: CatalogRepository,
    private val searchRepository: IntentSearchRepository,
    private val slotExtractor: SlotExtractor,
    private val confidenceScorer: ConfidenceScorer,
    private val learningEngine: LearningEngine,
    private val graphEngine: IntentGraphEngine,
    private val intentPlanner: IntentPlanner
) : IntentEngine {

    private val _currentState = MutableStateFlow<IntentState>(IntentState.Idle)
    override val currentState: StateFlow<IntentState> = _currentState.asStateFlow()

    override suspend fun processInput(naturalLanguageInput: String, context: ContextSnapshot?): IntentObject {
        val rawInput = naturalLanguageInput.trim()
        _currentState.value = IntentState.ProcessingInput(rawInput)

        val contextObj = context?.let {
            ContextObject(
                timestamp = it.timestamp,
                currentTimeString = it.currentTimeString,
                currentDateString = it.currentDateString,
                latitude = it.latitude,
                longitude = it.longitude,
                locationName = it.locationName,
                recentContacts = it.recentContacts,
                installedApps = it.installedApps,
                clipboardText = it.clipboardText,
                recentIntentIds = it.recentIntentIds,
                userPreferences = it.userPreferences
            )
        }

        // 1. Intent Search & Personalization
        val candidateMatches = searchRepository.searchIntents(rawInput, limit = 5)
        val personalizedMatches = learningEngine.personalizeRanking(candidateMatches)

        // 2. Intent Understanding -> IntentGraph Construction
        val intentGraph = graphEngine.buildGraph(rawQuery = rawInput, candidateMatches = personalizedMatches, context = context)

        // 3. Intent Planner Traversal -> IntentObject Generation
        val plannedIntents = intentPlanner.planGraph(intentGraph)
        val primaryIntent = plannedIntents.firstOrNull() ?: IntentObject(
            id = UUID.randomUUID().toString(),
            intentId = "unknown.fallback",
            domain = "general",
            confidence = 0.0f,
            context = contextObj
        )

        if (primaryIntent.intentId == "unknown.fallback") {
            _currentState.value = IntentState.Error("No matching intent found", "Unrecognized input")
            return primaryIntent
        }

        val targetIntentDef = catalogRepository.getIntentById(primaryIntent.intentId)
        val catalogEntities = catalogRepository.activeCatalogData.value?.entities ?: emptyList()

        // 4. Slot Extraction & Normalization
        val extractionResult = if (targetIntentDef != null) {
            slotExtractor.extractSlots(rawInput, targetIntentDef, catalogEntities)
        } else {
            SlotExtractionResult(emptyMap(), emptyList())
        }

        val overallConfidence = if (targetIntentDef != null) {
            confidenceScorer.calculateConfidence(
                intentMatchScore = primaryIntent.confidence,
                intent = targetIntentDef,
                extractedSlots = extractionResult.extractedSlots
            )
        } else primaryIntent.confidence

        val finalIntentObject = primaryIntent.copy(
            slots = extractionResult.extractedSlots,
            missingSlots = extractionResult.missingRequiredSlots,
            confidence = overallConfidence,
            context = contextObj
        )

        // 5. Record Usage for On-Device Learning
        learningEngine.recordIntentUsage(
            intentId = finalIntentObject.intentId,
            slots = extractionResult.extractedSlots.mapValues { it.value.rawValue ?: "" }
        )

        // 6. State Transition
        if (finalIntentObject.isComplete) {
            _currentState.value = IntentState.IntentIdentified(finalIntentObject, suggestions = personalizedMatches.drop(1).map { it.intent })
        } else {
            _currentState.value = IntentState.SlotFilling(finalIntentObject, activeSlotName = extractionResult.missingRequiredSlots.firstOrNull()?.slotName)
        }

        return finalIntentObject
    }

    override suspend fun updateSlot(intentObject: IntentObject, slotName: String, slotValue: String): IntentObject {
        val intentDef = catalogRepository.getIntentById(intentObject.intentId)

        val updatedSlots = intentObject.slots.toMutableMap()
        updatedSlots[slotName] = SlotValue(rawValue = slotValue, displayValue = slotValue)

        val remainingMissing = intentObject.missingSlots.filter { it.slotName != slotName }

        val recalculatedConfidence = if (intentDef != null) {
            confidenceScorer.calculateConfidence(
                intentMatchScore = intentObject.confidence,
                intent = intentDef,
                extractedSlots = updatedSlots
            )
        } else {
            intentObject.confidence
        }

        val updatedIntentObject = intentObject.copy(
            slots = updatedSlots,
            missingSlots = remainingMissing,
            confidence = recalculatedConfidence
        )

        learningEngine.recordIntentUsage(
            intentId = updatedIntentObject.intentId,
            slots = mapOf(slotName to slotValue)
        )

        if (updatedIntentObject.isComplete) {
            _currentState.value = IntentState.IntentIdentified(updatedIntentObject)
        } else {
            _currentState.value = IntentState.SlotFilling(updatedIntentObject, activeSlotName = remainingMissing.firstOrNull()?.slotName)
        }

        return updatedIntentObject
    }

    override fun resetState() {
        _currentState.value = IntentState.Idle
    }
}
