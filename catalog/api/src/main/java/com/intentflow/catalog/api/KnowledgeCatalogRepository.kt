package com.intentflow.catalog.api

import com.intentflow.core.model.Domain
import com.intentflow.core.model.IntentDefinition
import kotlinx.coroutines.flow.Flow

/**
 * Base interface for querying the Knowledge Catalog across all domains, intents, and utterances.
 */
interface KnowledgeCatalogRepository {
    fun getAllDomains(): Flow<List<Domain>>
    fun getIntentsForDomain(domainId: String): Flow<List<IntentDefinition>>
    suspend fun getIntentById(intentId: String): IntentDefinition?
    suspend fun searchUtterances(query: String, limit: Int = 20): List<IntentDefinition>
}
