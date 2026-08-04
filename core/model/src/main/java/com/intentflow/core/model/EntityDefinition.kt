package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Immutable schema for an Entity definition in the Knowledge Catalog.
 * Entities represent reusable domain values (e.g. Contact, City, Currency, AppName).
 * Fully serializable via Kotlin Serialization.
 */
@Serializable
data class EntityDefinition(
    val entityId: String,
    val displayName: String,
    val description: String = "",
    val values: List<String> = emptyList(),
    val synonyms: Map<String, List<String>> = emptyMap()
) {
    /** Validates this entity definition's required fields. */
    fun validate(): ValidationResult {
        if (entityId.isBlank()) {
            return ValidationResult.Invalid("entityId cannot be blank")
        }
        if (displayName.isBlank()) {
            return ValidationResult.Invalid("displayName cannot be blank for entity '$entityId'")
        }
        return ValidationResult.Valid
    }
}
