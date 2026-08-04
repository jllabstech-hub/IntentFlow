package com.intentflow.catalog.runtime

import com.intentflow.catalog.api.CatalogCache
import com.intentflow.core.model.ActionDefinition
import com.intentflow.core.model.CapabilityDescriptor
import com.intentflow.core.model.CatalogData
import com.intentflow.core.model.DependencyDefinition
import com.intentflow.core.model.DomainDefinition
import com.intentflow.core.model.EntityDefinition
import com.intentflow.core.model.ExecutionRule
import com.intentflow.core.model.IntentDefinition
import com.intentflow.core.model.IntentSchema
import com.intentflow.core.model.PermissionDefinition
import com.intentflow.core.model.SlotDefinition
import com.intentflow.core.model.UISchema
import com.intentflow.core.model.ValidationRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThreadSafeCatalogCache @Inject constructor() : CatalogCache {

    private val _activeCatalogData = MutableStateFlow<CatalogData?>(null)
    val activeCatalogData: StateFlow<CatalogData?> = _activeCatalogData.asStateFlow()

    private val domainMap = ConcurrentHashMap<String, DomainDefinition>()
    private val intentMap = ConcurrentHashMap<String, IntentDefinition>()
    private val entityMap = ConcurrentHashMap<String, EntityDefinition>()
    private val slotMap = ConcurrentHashMap<String, MutableList<SlotDefinition>>()
    private val capabilityMap = ConcurrentHashMap<String, CapabilityDescriptor>()
    private val schemaMap = ConcurrentHashMap<String, IntentSchema>()
    private val uiSchemaMap = ConcurrentHashMap<String, UISchema>()
    private val executionRuleMap = ConcurrentHashMap<String, ExecutionRule>()
    private val permissionMap = ConcurrentHashMap<String, MutableList<PermissionDefinition>>()
    private val actionMap = ConcurrentHashMap<String, MutableList<ActionDefinition>>()
    private val validationMap = ConcurrentHashMap<String, MutableList<ValidationRule>>()
    private val dependencyMap = ConcurrentHashMap<String, MutableList<DependencyDefinition>>()

    override fun populate(catalogData: CatalogData) {
        putCatalogData(catalogData)
    }

    fun putCatalogData(data: CatalogData) {
        _activeCatalogData.value = data

        clear()

        data.domains.forEach { domainMap[it.domainId] = it }
        data.domains.flatMap { it.intents }.forEach { intentMap[it.intentId] = it }
        data.intents.forEach { intentMap[it.intentId] = it }
        data.entities.forEach { entityMap[it.entityId] = it }
        data.slots.forEach { slot ->
            val list = slotMap.getOrPut(slot.intentId) { mutableListOf() }
            list.add(slot)
        }
        data.capabilities.forEach { capabilityMap[it.capabilityId] = it }
        data.intentSchemas.forEach { schemaMap[it.intentId] = it }
        data.uiSchemas.forEach { uiSchemaMap[it.intentId] = it }
        data.executionRules.forEach { executionRuleMap[it.intentId] = it }
        data.permissions.forEach { perm ->
            val list = permissionMap.getOrPut(perm.permissionId) { mutableListOf() }
            list.add(perm)
        }
        data.actions.forEach { act ->
            val list = actionMap.getOrPut(act.intentId) { mutableListOf() }
            list.add(act)
        }
        data.validationRules.forEach { valRule ->
            val list = validationMap.getOrPut(valRule.slotName) { mutableListOf() }
            list.add(valRule)
        }
        data.dependencies.forEach { dep ->
            val list = dependencyMap.getOrPut(dep.sourceIntentId) { mutableListOf() }
            list.add(dep)
        }
    }

    override fun getDomain(domainId: String): DomainDefinition? = domainMap[domainId]
    override fun getAllDomains(): List<DomainDefinition> = domainMap.values.toList()
    override fun getIntent(intentId: String): IntentDefinition? = intentMap[intentId]
    override fun getIntentsForDomain(domainId: String): List<IntentDefinition> = intentMap.values.filter { it.domain == domainId }
    override fun getEntity(entityId: String): EntityDefinition? = entityMap[entityId]
    override fun getAllEntities(): List<EntityDefinition> = entityMap.values.toList()

    override fun searchUtterances(query: String, limit: Int): List<IntentDefinition> {
        val q = query.lowercase().trim()
        return intentMap.values.filter {
            it.name.lowercase().contains(q) ||
                    it.intentId.lowercase().contains(q) ||
                    it.examples.any { ex -> ex.lowercase().contains(q) }
        }.take(limit)
    }

    override fun clear() {
        domainMap.clear()
        intentMap.clear()
        entityMap.clear()
        slotMap.clear()
        capabilityMap.clear()
        schemaMap.clear()
        uiSchemaMap.clear()
        executionRuleMap.clear()
        permissionMap.clear()
        actionMap.clear()
        validationMap.clear()
        dependencyMap.clear()
    }

    override fun isPopulated(): Boolean = _activeCatalogData.value != null

    fun getSlotsForIntent(intentId: String): List<SlotDefinition> = slotMap[intentId] ?: emptyList()
    fun getIntentSchema(intentId: String): IntentSchema? = schemaMap[intentId]
    fun getUiSchema(intentId: String): UISchema? = uiSchemaMap[intentId]
    fun getExecutionRule(intentId: String): ExecutionRule? = executionRuleMap[intentId]
    fun getCapabilities(): List<CapabilityDescriptor> = capabilityMap.values.toList()
    fun getPermissions(intentId: String): List<PermissionDefinition> = permissionMap[intentId] ?: emptyList()
    fun getActions(intentId: String): List<ActionDefinition> = actionMap[intentId] ?: emptyList()
    fun getValidationRules(slotName: String): List<ValidationRule> = validationMap[slotName] ?: emptyList()
    fun getDependencies(intentId: String): List<DependencyDefinition> = dependencyMap[intentId] ?: emptyList()
}
