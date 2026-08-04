package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Immutable DomainDefinition grouping related intent definitions.
 * Fully serializable via Kotlin Serialization.
 */
@Serializable
data class DomainDefinition(
    val id: String,
    val displayName: String,
    val description: String,
    val iconName: String? = null,
    val intents: List<IntentDefinition> = emptyList(),
    val version: String = "1.0.0"
) {
    /** Alias for domain identifier to maintain compatibility across catalog engines. */
    val domainId: String get() = id

    /**
     * Validates DomainDefinition fields and child intents.
     */
    fun validate(): ValidationResult {
        if (id.isBlank()) {
            return ValidationResult.Invalid("Domain id cannot be blank")
        }
        if (displayName.isBlank()) {
            return ValidationResult.Invalid("Domain displayName cannot be blank")
        }
        for (intent in intents) {
            val intentValidation = intent.validate()
            if (intentValidation is ValidationResult.Invalid) {
                return ValidationResult.Invalid("Invalid intent in domain '$id': ${intentValidation.reason}")
            }
        }
        return ValidationResult.Valid
    }
}
