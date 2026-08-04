package com.intentflow.catalog.runtime

import android.content.Context
import com.intentflow.catalog.api.CatalogLoader
import com.intentflow.core.common.dispatcher.DispatcherProvider
import com.intentflow.core.common.result.IntentResult
import com.intentflow.core.model.CatalogData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thread-safe Catalog Loader implementation reading catalog data asynchronously.
 */
@Singleton
class DefaultCatalogLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val jsonParser: CatalogJsonParser,
    private val dispatchers: DispatcherProvider
) : CatalogLoader {

    override suspend fun loadBundledCatalog(assetPath: String): IntentResult<CatalogData> {
        return withContext(dispatchers.io) {
            try {
                val jsonString = context.assets.open(assetPath).bufferedReader().use { it.readText() }
                val catalogData = jsonParser.parseCatalogJson(jsonString)
                IntentResult.Success(catalogData)
            } catch (e: Exception) {
                IntentResult.Error(e, "Failed to load bundled catalog from asset '$assetPath': ${e.message}")
            }
        }
    }

    override suspend fun loadDownloadedCatalog(filePath: String): IntentResult<CatalogData> {
        return withContext(dispatchers.io) {
            try {
                val file = File(filePath)
                if (!file.exists()) {
                    return@withContext IntentResult.Error(
                        IllegalArgumentException("File not found"),
                        "Downloaded catalog file does not exist at path '$filePath'"
                    )
                }
                val jsonString = file.readText()
                val catalogData = jsonParser.parseCatalogJson(jsonString)
                IntentResult.Success(catalogData)
            } catch (e: Exception) {
                IntentResult.Error(e, "Failed to load downloaded catalog from path '$filePath': ${e.message}")
            }
        }
    }
}
