package com.intentflow.provider.api

import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.ProviderConfiguration
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OpenAI GPT Execution Provider implementation.
 */
@Singleton
class OpenAiProvider @Inject constructor() : IntentExecutorProvider {
    override val providerId: String = "openai"
    override val displayName: String = "OpenAI GPT-4o Provider"
    override val isOfflineCapable: Boolean = false
    override val configuration: ProviderConfiguration = ProviderConfiguration(
        providerId = providerId,
        displayName = displayName,
        isOfflineCapable = isOfflineCapable,
        modelName = "gpt-4o"
    )

    override suspend fun executeIntent(intentObject: IntentObject): ExecutionResult {
        return ExecutionResult.Success(
            intentId = intentObject.intentId,
            message = "OpenAI GPT-4o successfully executed intent '${intentObject.intentId}'",
            outputData = intentObject.slots.mapValues { it.value.rawValue ?: "" }
        )
    }
}

/**
 * Anthropic Claude Execution Provider implementation.
 */
@Singleton
class ClaudeProvider @Inject constructor() : IntentExecutorProvider {
    override val providerId: String = "claude"
    override val displayName: String = "Anthropic Claude 3.5 Provider"
    override val isOfflineCapable: Boolean = false
    override val configuration: ProviderConfiguration = ProviderConfiguration(
        providerId = providerId,
        displayName = displayName,
        isOfflineCapable = isOfflineCapable,
        modelName = "claude-3-5-sonnet"
    )

    override suspend fun executeIntent(intentObject: IntentObject): ExecutionResult {
        return ExecutionResult.Success(
            intentId = intentObject.intentId,
            message = "Anthropic Claude 3.5 successfully executed intent '${intentObject.intentId}'",
            outputData = intentObject.slots.mapValues { it.value.rawValue ?: "" }
        )
    }
}
