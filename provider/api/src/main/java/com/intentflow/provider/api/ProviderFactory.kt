package com.intentflow.provider.api

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Factory for instantiating and resolving AI / System Execution Providers by providerId.
 */
@Singleton
class ProviderFactory @Inject constructor(
    private val mockProvider: IntentExecutorProvider, // Default fallback
    private val providers: Set<@JvmSuppressWildcards IntentExecutorProvider>
) {

    fun getProvider(providerId: String): IntentExecutorProvider {
        return providers.firstOrNull { it.providerId.equals(providerId, ignoreCase = true) }
            ?: mockProvider
    }

    fun getAllAvailableProviders(): List<IntentExecutorProvider> {
        return providers.toList()
    }
}
