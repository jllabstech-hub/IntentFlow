package com.intentflow.core.datastore

import kotlinx.coroutines.flow.Flow

/**
 * Interface for user preferences and local learning storage.
 */
interface PreferenceRepository {
    val selectedProviderId: Flow<String>
    val developerModeEnabled: Flow<Boolean>

    suspend fun setSelectedProviderId(providerId: String)
    suspend fun setDeveloperModeEnabled(enabled: Boolean)
}
