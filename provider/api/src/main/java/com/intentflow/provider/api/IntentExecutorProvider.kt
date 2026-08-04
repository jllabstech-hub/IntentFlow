package com.intentflow.provider.api

import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.ProviderConfiguration

/**
 * Common interface implemented by all AI and Execution Providers in IntentFlow.
 * Providers are execution engines—not the core intelligence.
 */
interface IntentExecutorProvider {
    val providerId: String
    val displayName: String
    val isOfflineCapable: Boolean
    val configuration: ProviderConfiguration

    suspend fun executeIntent(intentObject: IntentObject): ExecutionResult
}
