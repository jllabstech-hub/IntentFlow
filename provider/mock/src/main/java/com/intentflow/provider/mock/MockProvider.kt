package com.intentflow.provider.mock

import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.ProviderConfiguration
import com.intentflow.provider.api.IntentExecutorProvider
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Deterministic offline Mock Provider for testing and development.
 * Returns a pre-defined successful result for any intent without network access.
 * The Mock Provider is the default provider in the [ProviderManager] registration.
 */
@Singleton
class MockProvider @Inject constructor() : IntentExecutorProvider {
    override val providerId: String = "mock"
    override val displayName: String = "Mock Offline Provider"
    override val isOfflineCapable: Boolean = true
    override val configuration: ProviderConfiguration = ProviderConfiguration(
        providerId = "mock",
        displayName = "Mock Offline Provider",
        isOfflineCapable = true
    )

    override suspend fun executeIntent(intentObject: IntentObject): ExecutionResult {
        return ExecutionResult.Success(
            intentId = intentObject.intentId,
            message = "Mock execution completed successfully for ${intentObject.intentId}",
            providerId = providerId,
            outputData = intentObject.slots.mapValues { it.value.rawValue?.toString() ?: "" }
        )
    }
}
