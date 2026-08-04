package com.intentflow.engine.search

import com.intentflow.catalog.api.KnowledgeCatalogRepository
import com.intentflow.core.model.IntentDefinition
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FtsSearchEngine @Inject constructor(
    private val catalogRepository: KnowledgeCatalogRepository
) : SearchEngine {

    override suspend fun suggestIntents(partialText: String, limit: Int): List<IntentDefinition> {
        if (partialText.isBlank()) return emptyList()
        return catalogRepository.searchUtterances(query = partialText.trim(), limit = limit)
    }
}
