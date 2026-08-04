package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Represents the validation result of a slot value.
 */
@Serializable
sealed class ValidationResult {
    @Serializable
    data object Valid : ValidationResult()

    @Serializable
    data class Invalid(val reason: String) : ValidationResult()

    @Serializable
    data object Unchecked : ValidationResult()
}
