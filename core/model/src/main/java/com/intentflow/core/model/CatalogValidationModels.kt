package com.intentflow.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class ValidationErrorSeverity {
    INFO,
    WARNING,
    ERROR,
    CRITICAL_BLOCKER
}

@Serializable
enum class ValidationErrorCategory {
    DUPLICATE_ID,
    BROKEN_REFERENCE,
    MISSING_SLOT,
    UNUSED_ENTITY,
    CIRCULAR_DEPENDENCY,
    REGEX_SYNTAX_ERROR,
    PERMISSION_CONFLICT,
    SCHEMA_INCOMPATIBILITY,
    VERSION_INCOMPATIBILITY
}

@Serializable
data class ValidationError(
    val errorId: String,
    val category: ValidationErrorCategory,
    val severity: ValidationErrorSeverity,
    val targetEntityId: String,
    val message: String,
    val recommendation: String? = null
)

@Serializable
data class DetailedValidationReport(
    val reportId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val catalogVersionCode: Int,
    val isValid: Boolean,
    val criticalErrorCount: Int,
    val warningCount: Int,
    val errors: List<ValidationError> = emptyList()
)
