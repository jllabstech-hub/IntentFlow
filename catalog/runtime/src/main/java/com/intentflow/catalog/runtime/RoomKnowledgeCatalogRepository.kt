package com.intentflow.catalog.runtime

import com.intentflow.catalog.api.KnowledgeCatalogRepository
import com.intentflow.core.database.dao.CatalogDao
import com.intentflow.core.model.Domain
import com.intentflow.core.model.IntentDefinition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomKnowledgeCatalogRepository @Inject constructor(
    private val catalogDao: CatalogDao,
    private val json: Json
) : KnowledgeCatalogRepository {

    override fun getAllDomains(): Flow<List<Domain>> {
        return catalogDao.getAllDomains().map { entities ->
            entities.map { entity ->
                Domain(
                    id = entity.id,
                    displayName = entity.displayName,
                    description = entity.description,
                    iconName = entity.iconName
                )
            }
        }
    }

    override fun getIntentsForDomain(domainId: String): Flow<List<IntentDefinition>> {
        return catalogDao.getIntentsForDomain(domainId).map { entities ->
            entities.map { entity ->
                IntentDefinition(
                    intentId = entity.intentId,
                    name = entity.displayName,
                    description = entity.description,
                    domain = entity.domainId,
                    examples = parseList(entity.exampleUtterancesJson)
                )
            }
        }
    }

    override suspend fun getIntentById(intentId: String): IntentDefinition? {
        val entity = catalogDao.getIntentById(intentId) ?: return null
        return IntentDefinition(
            intentId = entity.intentId,
            name = entity.displayName,
            description = entity.description,
            domain = entity.domainId,
            examples = parseList(entity.exampleUtterancesJson)
        )
    }

    override suspend fun searchUtterances(query: String, limit: Int): List<IntentDefinition> {
        val matches = catalogDao.searchUtterancesFts("*$query*", limit)
        return matches.mapNotNull { fts ->
            getIntentById(fts.intentId)
        }
    }

    private fun parseList(jsonString: String): List<String> {
        return try {
            json.decodeFromString(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
