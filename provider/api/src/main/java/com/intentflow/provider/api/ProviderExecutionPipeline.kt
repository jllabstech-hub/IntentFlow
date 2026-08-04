package com.intentflow.provider.api

import com.intentflow.core.common.dispatcher.DispatcherProvider
import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Execution pipeline routing IntentObjects to the active provider with timeout & fallback handling.
 */
@Singleton
class ProviderExecutionPipeline @Inject constructor(
    private val providerManager: ProviderManager,
    private val dispatchers: DispatcherProvider
) {

    suspend fun execute(intentObject: IntentObject, targetProviderId: String? = null): ExecutionResult {
        return withContext(dispatchers.io) {
            val provider = if (!targetProviderId.isNullOrBlank()) {
                providerManager.getRegisteredProviders().firstOrNull { it.providerId == targetProviderId }
                    ?: providerManager.activeProvider.value
            } else {
                providerManager.activeProvider.value
            }

            try {
                provider.executeIntent(intentObject)
            } catch (e: Exception) {
                // Fall back to Mock Provider if active online provider fails
                val fallbackProvider = providerManager.getRegisteredProviders().firstOrNull { it.isOfflineCapable }
                if (fallbackProvider != null && fallbackProvider.providerId != provider.providerId) {
                    fallbackProvider.executeIntent(intentObject)
                } else {
                    ExecutionResult.Failure(
                        intentId = intentObject.intentId,
                        errorMessage = "Provider '${provider.providerId}' execution failed: ${e.message}",
                        cause = e.javaClass.simpleName
                    )
                }
            }
        }
    }
}
