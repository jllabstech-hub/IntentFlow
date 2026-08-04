package com.intentflow.engine.search

/**
 * Interface allowing future extension for AI / Vector Embedding Semantic Search.
 * Pluggable alongside local offline keyword and fuzzy matchers.
 */
interface SemanticSearchProvider {
    val isAvailable: Boolean
    suspend fun searchSemantic(query: String, limit: Int = 10): List<ScoredIntentMatch>
}

/**
 * Default fallback implementation when no vector embedding model is loaded.
 */
class NoOpSemanticSearchProvider : SemanticSearchProvider {
    override val isAvailable: Boolean = false
    override suspend fun searchSemantic(query: String, limit: Int): List<ScoredIntentMatch> = emptyList()
}
