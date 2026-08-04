package com.intentflow.catalog.api

import com.intentflow.core.model.ActionDefinition
import com.intentflow.core.model.CapabilityDescriptor
import com.intentflow.core.model.CatalogData
import com.intentflow.core.model.DependencyDefinition
import com.intentflow.core.model.DomainDefinition
import com.intentflow.core.model.ExecutionRule
import com.intentflow.core.model.IntentDefinition
import com.intentflow.core.model.IntentSchema
import com.intentflow.core.model.PermissionDefinition
import com.intentflow.core.model.UISchema
import com.intentflow.core.model.ValidationRule
import kotlinx.coroutines.flow.StateFlow

/**
 * Single Source of Truth Repository Interface covering static definitions, behavioral contracts, capabilities, and execution rules.
 */
interface CatalogRepository {
    val activeCatalogData: StateFlow<CatalogData?>

    suspend fun loadCatalog(sourcePath: String? = null): Boolean

    // Static Queries
    suspend fun getDomainById(domainId: String): DomainDefinition?
    suspend fun getIntentById(intentId: String): IntentDefinition?
    suspend fun searchIntentsByQuery(query: String): List<IntentDefinition>

    // Behavioral & UI Queries
    suspend fun getIntentSchema(intentId: String): IntentSchema?
    suspend fun getUiSchema(intentId: String): UISchema?
    suspend fun getValidationRules(slotName: String): List<ValidationRule>

    // Capability & Execution Queries
    suspend fun getCapabilities(): List<CapabilityDescriptor>
    suspend fun getExecutionRule(intentId: String): ExecutionRule?
    suspend fun getPermissions(intentId: String): List<PermissionDefinition>
    suspend fun getActions(intentId: String): List<ActionDefinition>
    suspend fun getDependencies(intentId: String): List<DependencyDefinition>
}
