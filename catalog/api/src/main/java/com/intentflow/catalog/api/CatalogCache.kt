package com.intentflow.catalog.api

import com.intentflow.core.model.CatalogData
import com.intentflow.core.model.DomainDefinition
import com.intentflow.core.model.EntityDefinition
import com.intentflow.core.model.IntentDefinition

/**
 * Thread-safe in-memory cache indexing catalog domains, intents, slots, utterances, and entities for sub-millisecond fast lookup.
 */
interface CatalogCache {
    fun populate(catalogData: CatalogData)
    fun getDomain(domainId: String): DomainDefinition?
    fun getAllDomains(): List<DomainDefinition>
    fun getIntent(intentId: String): IntentDefinition?
    fun getIntentsForDomain(domainId: String): List<IntentDefinition>
    fun getEntity(entityId: String): EntityDefinition?
    fun getAllEntities(): List<EntityDefinition>
    fun searchUtterances(query: String, limit: Int = 20): List<IntentDefinition>
    fun clear()
    fun isPopulated(): Boolean
}
