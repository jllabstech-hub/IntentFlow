package com.intentflow.engine.search

import com.intentflow.core.model.IntentDefinition

/**
 * High-performance offline Search Engine interface.
 * Supports prefix, contains, fuzzy, keyword, and ranking across all catalog intents.
 */
interface SearchEngine {
    suspend fun suggestIntents(partialText: String, limit: Int = 10): List<IntentDefinition>
}
