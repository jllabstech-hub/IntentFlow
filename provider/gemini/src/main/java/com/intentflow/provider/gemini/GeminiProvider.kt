package com.intentflow.provider.gemini

import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.ProviderConfiguration
import com.intentflow.provider.api.IntentExecutorProvider
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Google Gemini Cloud AI Execution Provider.
 */
@Singleton
class GeminiProvider @Inject constructor() : IntentExecutorProvider {
    override val providerId: String = "gemini"
    override val displayName: String = "Google Gemini Provider"
    override val isOfflineCapable: Boolean = false
    override val configuration: ProviderConfiguration = ProviderConfiguration(
        providerId = providerId,
        displayName = displayName,
        isOfflineCapable = isOfflineCapable,
        modelName = "gemini-1.5-flash"
    )

    override suspend fun executeIntent(intentObject: IntentObject): ExecutionResult {
        return ExecutionResult.Success(
            intentId = intentObject.intentId,
            message = "Google Gemini Flash executed intent '${intentObject.intentId}'",
            outputData = intentObject.slots.mapValues { it.value.rawValue ?: "" }
        )
    }
}
