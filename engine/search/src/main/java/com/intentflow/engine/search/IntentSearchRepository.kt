package com.intentflow.engine.search

import com.intentflow.catalog.api.CatalogRepository
import com.intentflow.core.model.IntentDefinition
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository interface for Intent Search Engine.
 */
interface IntentSearchRepository {
    suspend fun searchIntents(query: String, limit: Int = 10): List<ScoredIntentMatch>
}

/**
 * Production-ready implementation of IntentSearchRepository.
 * Supports Prefix, Contains, Token Overlap, Fuzzy Levenshtein, and pluggable Semantic Search.
 */
@Singleton
class DefaultIntentSearchRepository @Inject constructor(
    private val catalogRepository: CatalogRepository,
    private val ranker: IntentRanker,
    private val semanticSearchProvider: SemanticSearchProvider
) : IntentSearchRepository {

    override suspend fun searchIntents(query: String, limit: Int): List<ScoredIntentMatch> {
        if (query.isBlank()) return emptyList()

        val candidateMatches = mutableListOf<ScoredIntentMatch>()

        // 1. Direct FTS & Catalog search candidates
        val ftsCandidates = catalogRepository.searchIntentsByQuery(query)
        for (intent in ftsCandidates) {
            for (utterance in intent.examples) {
                candidateMatches.add(ranker.rankMatch(query, intent, utterance))
            }
        }

        // 2. Fallback scan across all active domains if FTS results are sparse
        if (candidateMatches.size < limit) {
            val allDomains = catalogRepository.activeCatalogData.value?.domains ?: emptyList()
            for (domain in allDomains) {
                for (intent in domain.intents) {
                    for (utterance in intent.examples) {
                        val match = ranker.rankMatch(query, intent, utterance)
                        if (match.score > 0.20f) {
                            candidateMatches.add(match)
                        }
                    }
                }
            }
        }

        // 3. Pluggable Future Semantic Search Integration
        if (semanticSearchProvider.isAvailable) {
            val semanticResults = semanticSearchProvider.searchSemantic(query, limit)
            candidateMatches.addAll(semanticResults)
        }

        // 4. Merge, deduplicate by intent ID, and return Top K ranked matches
        return ranker.mergeAndRankIntents(candidateMatches, limit)
    }
}
