package com.intentflow.catalog.api

import com.intentflow.core.model.CatalogVersion
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for managing multiple Catalog Versions (bundled, downloaded, active version switching).
 */
interface CatalogVersionManager {
    val activeVersion: StateFlow<CatalogVersion?>
    val availableVersions: StateFlow<List<CatalogVersion>>

    suspend fun registerVersion(version: CatalogVersion, sourcePath: String)
    suspend fun setActiveVersion(versionCode: Int): Boolean
    suspend fun getActiveVersionPath(): String?
}
