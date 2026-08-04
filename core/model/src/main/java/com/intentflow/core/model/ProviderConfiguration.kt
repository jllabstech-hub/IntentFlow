package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Immutable configuration for an AI or System Provider.
 * Fully serializable via Kotlin Serialization.
 */
@Serializable
data class ProviderConfiguration(
    val providerId: String,
    val displayName: String,
    val isOfflineCapable: Boolean = true,
    val apiKey: String? = null,
    val modelName: String? = null,
    val endpointUrl: String? = null,
    val temperature: Float = 0.7f,
    val maxTokens: Int = 1024,
    val additionalParameters: Map<String, String> = emptyMap()
) {
    /**
     * Validates provider configuration parameters.
     */
    fun validate(): ValidationResult {
        if (providerId.isBlank()) {
            return ValidationResult.Invalid("providerId cannot be blank")
        }
        if (displayName.isBlank()) {
            return ValidationResult.Invalid("displayName cannot be blank")
        }
        if (temperature !in 0.0f..2.0f) {
            return ValidationResult.Invalid("temperature must be between 0.0 and 2.0")
        }
        if (maxTokens <= 0) {
            return ValidationResult.Invalid("maxTokens must be greater than 0")
        }
        if (!isOfflineCapable && endpointUrl.isNullOrBlank() && apiKey.isNullOrBlank()) {
            return ValidationResult.Invalid("Online provider requires an endpointUrl or apiKey")
        }
        return ValidationResult.Valid
    }
}
