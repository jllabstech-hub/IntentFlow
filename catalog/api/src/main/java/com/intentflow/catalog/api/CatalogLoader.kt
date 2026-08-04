package com.intentflow.catalog.api

import com.intentflow.core.model.CatalogData
import com.intentflow.core.common.result.IntentResult

/**
 * Interface for loading Catalog JSON data from bundled Android assets or downloaded local disk paths.
 */
interface CatalogLoader {
    suspend fun loadBundledCatalog(assetPath: String = "catalog/catalog_v1.json"): IntentResult<CatalogData>
    suspend fun loadDownloadedCatalog(filePath: String): IntentResult<CatalogData>
}
