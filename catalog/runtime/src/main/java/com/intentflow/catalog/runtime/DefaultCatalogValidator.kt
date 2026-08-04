package com.intentflow.catalog.runtime

import com.intentflow.catalog.api.CatalogValidator
import com.intentflow.core.model.CatalogData
import com.intentflow.core.model.ValidationResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Validates whole CatalogData for duplicate domain IDs, duplicate intent IDs, and field bounds.
 */
@Singleton
class DefaultCatalogValidator @Inject constructor() : CatalogValidator {

    override fun validate(catalogData: CatalogData): ValidationResult {
        val versionValidation = catalogData.version.validate()
        if (versionValidation is ValidationResult.Invalid) {
            return ValidationResult.Invalid("Invalid catalog version: ${versionValidation.reason}")
        }

        val domainIds = mutableSetOf<String>()
        val intentIds = mutableSetOf<String>()
        val entityIds = mutableSetOf<String>()

        for (domain in catalogData.domains) {
            val domainValidation = domain.validate()
            if (domainValidation is ValidationResult.Invalid) {
                return domainValidation
            }
            if (!domainIds.add(domain.id)) {
                return ValidationResult.Invalid("Duplicate domain ID found: '${domain.id}'")
            }

            for (intent in domain.intents) {
                if (!intentIds.add(intent.intentId)) {
                    return ValidationResult.Invalid("Duplicate intent ID found: '${intent.intentId}'")
                }
            }
        }

        for (entity in catalogData.entities) {
            val entityValidation = entity.validate()
            if (entityValidation is ValidationResult.Invalid) {
                return entityValidation
            }
            if (!entityIds.add(entity.entityId)) {
                return ValidationResult.Invalid("Duplicate entity ID found: '${entity.entityId}'")
            }
        }

        return ValidationResult.Valid
    }
}
