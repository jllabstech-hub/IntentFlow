package com.intentflow.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "intentflow_preferences")

@Singleton
class DataStorePreferenceRepository @Inject constructor(
    private val context: Context
) : PreferenceRepository {

    private object Keys {
        val SELECTED_PROVIDER = stringPreferencesKey("selected_provider_id")
        val DEVELOPER_MODE = booleanPreferencesKey("developer_mode_enabled")
    }

    override val selectedProviderId: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.SELECTED_PROVIDER] ?: "mock"
    }

    override val developerModeEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.DEVELOPER_MODE] ?: true
    }

    override suspend fun setSelectedProviderId(providerId: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SELECTED_PROVIDER] = providerId
        }
    }

    override suspend fun setDeveloperModeEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DEVELOPER_MODE] = enabled
        }
    }
}
