package com.intentflow.provider.api

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thread-safe Provider Manager enabling instant hot-swapping between AI providers with zero code changes.
 */
@Singleton
class ProviderManager @Inject constructor(
    private val factory: ProviderFactory
) {
    private val providerRegistry = ConcurrentHashMap<String, IntentExecutorProvider>()

    private val _activeProvider = MutableStateFlow<IntentExecutorProvider>(factory.getProvider("mock"))
    val activeProvider: StateFlow<IntentExecutorProvider> = _activeProvider.asStateFlow()

    init {
        factory.getAllAvailableProviders().forEach { registerProvider(it) }
    }

    fun registerProvider(provider: IntentExecutorProvider) {
        providerRegistry[provider.providerId] = provider
    }

    fun setActiveProvider(providerId: String): Boolean {
        val target = providerRegistry[providerId] ?: factory.getProvider(providerId)
        _activeProvider.value = target
        return true
    }

    fun getRegisteredProviders(): List<IntentExecutorProvider> {
        return providerRegistry.values.toList()
    }
}
