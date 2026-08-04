package com.intentflow.catalog.api

import com.intentflow.core.model.CatalogData
import com.intentflow.core.model.ValidationResult

/**
 * Interface for Catalog Validation.
 * Validates whole CatalogData instances for consistency, unique IDs, non-empty fields, and valid regexes.
 */
interface CatalogValidator {
    fun validate(catalogData: CatalogData): ValidationResult
}
