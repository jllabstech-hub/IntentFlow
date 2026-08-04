package com.intentflow.engine.search

import com.intentflow.core.model.IntentDefinition
import kotlinx.serialization.Serializable

/**
 * Categorizes how a search query matched an intent.
 */
@Serializable
enum class MatchType {
    EXACT,
    PREFIX,
    CONTAINS,
    TOKEN_OVERLAP,
    FUZZY,
    SEMANTIC
}

/**
 * Result model representing an intent match paired with its relevance score and match metadata.
 */
@Serializable
data class ScoredIntentMatch(
    val intent: IntentDefinition,
    val score: Float,
    val matchedUtterance: String? = null,
    val matchType: MatchType = MatchType.FUZZY
) : Comparable<ScoredIntentMatch> {
    override fun compareTo(other: ScoredIntentMatch): Int {
        return other.score.compareTo(this.score)
    }
}
