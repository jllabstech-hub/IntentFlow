package com.intentflow.catalog.runtime

import com.intentflow.catalog.api.CatalogRepository
import com.intentflow.core.common.dispatcher.DispatcherProvider
import com.intentflow.core.common.result.IntentResult
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
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultCatalogRepository @Inject constructor(
    private val catalogLoader: DefaultCatalogLoader,
    private val catalogCache: ThreadSafeCatalogCache,
    private val dispatchers: DispatcherProvider
) : CatalogRepository {

    override val activeCatalogData: StateFlow<CatalogData?> = catalogCache.activeCatalogData

    override suspend fun loadCatalog(sourcePath: String?): Boolean {
        return withContext(dispatchers.io) {
            val result = catalogLoader.loadBundledCatalog(sourcePath ?: "catalog-v1.json")
            if (result is IntentResult.Success) {
                catalogCache.putCatalogData(result.data)
                true
            } else false
        }
    }

    override suspend fun getDomainById(domainId: String): DomainDefinition? = catalogCache.getDomain(domainId)
    override suspend fun getIntentById(intentId: String): IntentDefinition? = catalogCache.getIntent(intentId)
    override suspend fun searchIntentsByQuery(query: String): List<IntentDefinition> = withContext(dispatchers.default) {
        catalogCache.searchUtterances(query)
    }

    override suspend fun getIntentSchema(intentId: String): IntentSchema? {
        val cached = catalogCache.getIntentSchema(intentId)
        if (cached != null) return cached

        val intentDef = catalogCache.getIntent(intentId) ?: return null
        val slots = catalogCache.getSlotsForIntent(intentId)

        return IntentSchema(
            intentId = intentDef.intentId,
            slots = slots,
            validationRules = mapOf("strictMode" to "true"),
            dependencies = emptyList(),
            defaultValues = slots.mapNotNull { s -> s.defaultValue?.let { s.slotName to it } }.toMap(),
            dynamicUiRules = mapOf("layout" to "vertical"),
            contextRules = mapOf("requiresLocation" to "false"),
            executionRules = mapOf("timeoutMs" to "5000"),
            permissionRequirements = emptyList()
        )
    }

    override suspend fun getUiSchema(intentId: String): UISchema? = catalogCache.getUiSchema(intentId)
    override suspend fun getValidationRules(slotName: String): List<ValidationRule> = catalogCache.getValidationRules(slotName)

    override suspend fun getCapabilities(): List<CapabilityDescriptor> = catalogCache.getCapabilities()
    override suspend fun getExecutionRule(intentId: String): ExecutionRule? = catalogCache.getExecutionRule(intentId)
    override suspend fun getPermissions(intentId: String): List<PermissionDefinition> = catalogCache.getPermissions(intentId)
    override suspend fun getActions(intentId: String): List<ActionDefinition> = catalogCache.getActions(intentId)
    override suspend fun getDependencies(intentId: String): List<DependencyDefinition> = catalogCache.getDependencies(intentId)
}
