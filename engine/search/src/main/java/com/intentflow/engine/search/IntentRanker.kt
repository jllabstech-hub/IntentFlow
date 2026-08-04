package com.intentflow.engine.search

import com.intentflow.core.model.IntentDefinition
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Keyword Ranking Engine computing multi-strategy relevance scores for intent candidate matches.
 *
 * Scoring Weights:
 * - Exact Match: 1.0
 * - Prefix Match: 0.85
 * - Token Overlap: 0.70
 * - Contains Substring: 0.60
 * - Fuzzy Levenshtein: 0.50
 */
@Singleton
class IntentRanker @Inject constructor() {

    fun rankMatch(query: String, intent: IntentDefinition, utterance: String): ScoredIntentMatch {
        val q = query.lowercase().trim()
        val u = utterance.lowercase().trim()

        if (q == u) {
            return ScoredIntentMatch(intent, score = 1.0f, matchedUtterance = utterance, matchType = MatchType.EXACT)
        }

        if (u.startsWith(q) || q.startsWith(u)) {
            val ratio = q.length.toFloat() / u.length.toFloat()
            val score = (0.85f * ratio).coerceIn(0.65f, 0.95f)
            return ScoredIntentMatch(intent, score = score, matchedUtterance = utterance, matchType = MatchType.PREFIX)
        }

        val jaccard = FuzzyMatcher.calculateTokenJaccardSimilarity(q, u)
        if (jaccard > 0.4f) {
            val score = 0.70f * jaccard
            return ScoredIntentMatch(intent, score = score, matchedUtterance = utterance, matchType = MatchType.TOKEN_OVERLAP)
        }

        if (u.contains(q) || q.contains(u)) {
            return ScoredIntentMatch(intent, score = 0.60f, matchedUtterance = utterance, matchType = MatchType.CONTAINS)
        }

        val levSim = FuzzyMatcher.calculateLevenshteinSimilarity(q, u)
        val score = (0.50f * levSim).coerceIn(0.0f, 0.55f)
        return ScoredIntentMatch(intent, score = score, matchedUtterance = utterance, matchType = MatchType.FUZZY)
    }

    fun mergeAndRankIntents(scoredMatches: List<ScoredIntentMatch>, limit: Int): List<ScoredIntentMatch> {
        val bestPerIntent = mutableMapOf<String, ScoredIntentMatch>()

        for (match in scoredMatches) {
            val existing = bestPerIntent[match.intent.intentId]
            if (existing == null || match.score > existing.score) {
                bestPerIntent[match.intent.intentId] = match
            }
        }

        return bestPerIntent.values
            .sortedByDescending { it.score }
            .take(limit)
    }
}
