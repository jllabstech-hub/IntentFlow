package com.intentflow.catalog.runtime

import com.intentflow.catalog.api.CatalogVersionManager
import com.intentflow.core.model.CatalogVersion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thread-safe Catalog Version Manager supporting bundled and downloaded catalog coexistence and switching.
 */
@Singleton
class DefaultCatalogVersionManager @Inject constructor() : CatalogVersionManager {

    private val _activeVersion = MutableStateFlow<CatalogVersion?>(null)
    override val activeVersion: StateFlow<CatalogVersion?> = _activeVersion.asStateFlow()

    private val _availableVersions = MutableStateFlow<List<CatalogVersion>>(emptyList())
    override val availableVersions: StateFlow<List<CatalogVersion>> = _availableVersions.asStateFlow()

    private val versionPathMap = ConcurrentHashMap<Int, String>()
    private val versionObjectMap = ConcurrentHashMap<Int, CatalogVersion>()

    override suspend fun registerVersion(version: CatalogVersion, sourcePath: String) {
        versionObjectMap[version.versionCode] = version
        versionPathMap[version.versionCode] = sourcePath

        val currentList = versionObjectMap.values.sortedByDescending { it.versionCode }
        _availableVersions.value = currentList

        if (_activeVersion.value == null || version.versionCode > (_activeVersion.value?.versionCode ?: 0)) {
            _activeVersion.value = version
        }
    }

    override suspend fun setActiveVersion(versionCode: Int): Boolean {
        val target = versionObjectMap[versionCode] ?: return false
        _activeVersion.value = target
        return true
    }

    override suspend fun getActiveVersionPath(): String? {
        val active = _activeVersion.value ?: return null
        return versionPathMap[active.versionCode]
    }
}
