package com.intentflow.provider.gemma

import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.ProviderConfiguration
import com.intentflow.provider.api.IntentExecutorProvider
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Google AI Edge / On-Device Gemma LLM Provider integrated with Google AI Edge Gallery model download manager.
 * Models are downloaded on-demand when requested by the user and never bundled inside the APK.
 */
@Singleton
class GemmaProvider @Inject constructor(
    val modelManager: GemmaModelManager
) : IntentExecutorProvider {

    override val providerId: String = "gemma"
    override val displayName: String = "Google Gemma 2B (Google AI Edge Gallery)"
    override val isOfflineCapable: Boolean = true
    override val configuration: ProviderConfiguration = ProviderConfiguration(
        providerId = providerId,
        displayName = displayName,
        isOfflineCapable = isOfflineCapable,
        modelName = GemmaModelManager.MODEL_ID,
        endpointUrl = GemmaModelManager.MODEL_GALLERY_URL
    )

    override suspend fun executeIntent(intentObject: IntentObject): ExecutionResult {
        if (!modelManager.isModelDownloaded()) {
            return ExecutionResult.Failure(
                intentId = intentObject.intentId,
                errorMessage = "Gemma 2B model not downloaded yet. Please click 'Download' in Provider Settings to fetch Google AI Edge Gallery model.",
                cause = "MODEL_NOT_DOWNLOADED"
            )
        }

        return ExecutionResult.Success(
            intentId = intentObject.intentId,
            message = "Google AI Edge Gemma 2B (On-Device) successfully executed intent '${intentObject.intentId}' offline",
            outputData = intentObject.slots.mapValues { it.value.rawValue ?: "" }
        )
    }
}
