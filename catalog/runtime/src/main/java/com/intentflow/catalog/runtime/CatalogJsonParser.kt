package com.intentflow.catalog.runtime

import com.intentflow.core.model.CatalogData
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thread-safe JSON Parser for Knowledge Catalog using Kotlin Serialization.
 */
@Singleton
class CatalogJsonParser @Inject constructor(
    private val json: Json
) {
    fun parseCatalogJson(jsonString: String): CatalogData {
        require(jsonString.isNotBlank()) { "Catalog JSON string cannot be blank" }
        return json.decodeFromString<CatalogData>(jsonString)
    }

    fun serializeCatalog(catalogData: CatalogData): String {
        return json.encodeToString(CatalogData.serializer(), catalogData)
    }
}
