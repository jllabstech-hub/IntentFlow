package com.intentflow.sdk

import android.content.Context
import com.intentflow.core.common.logger.IntentLogger
import com.intentflow.core.model.CapabilityDescriptor
import com.intentflow.core.model.DomainDefinition
import com.intentflow.core.model.IntentDefinition
import com.intentflow.engine.context.provider.KnowledgeProvider
import com.intentflow.engine.skill.IntentSkill
import com.intentflow.plugin.api.AndroidPlugin
import com.intentflow.provider.api.IntentExecutorProvider
import java.util.concurrent.ConcurrentHashMap

/**
 * Public SDK Entry Point for Third-Party Developers.
 * Hides internal engine complexity behind a clean, stable registration facade.
 */
object IntentFlowSdk {

    private var isInitialized: Boolean = false

    private val registeredDomains = ConcurrentHashMap<String, DomainDefinition>()
    private val registeredIntents = ConcurrentHashMap<String, IntentDefinition>()
    private val registeredSkills = ConcurrentHashMap<String, IntentSkill>()
    private val registeredCapabilities = ConcurrentHashMap<String, CapabilityDescriptor>()
    private val registeredKnowledgeProviders = ConcurrentHashMap<String, KnowledgeProvider<*>>()
    private val registeredPlugins = ConcurrentHashMap<String, AndroidPlugin>()
    private val registeredProviders = ConcurrentHashMap<String, IntentExecutorProvider>()
    private val registeredUiComponents = ConcurrentHashMap<String, Any>()

    fun initialize(context: Context) {
        isInitialized = true
        IntentLogger.d("IntentFlowSdk", "IntentFlow SDK Initialized Successfully")
    }

    fun registerDomain(domain: DomainDefinition) {
        registeredDomains[domain.domainId] = domain
        IntentLogger.d("IntentFlowSdk", "Registered Domain: ${domain.displayName}")
    }

    fun registerIntent(intent: IntentDefinition) {
        registeredIntents[intent.intentId] = intent
        IntentLogger.d("IntentFlowSdk", "Registered Intent: ${intent.name}")
    }

    fun registerSkill(skill: IntentSkill) {
        registeredSkills[skill.definition.skillId] = skill
        IntentLogger.d("IntentFlowSdk", "Registered Skill: ${skill.definition.displayName}")
    }

    fun registerCapability(capability: CapabilityDescriptor, executor: AndroidPlugin) {
        registeredCapabilities[capability.capabilityId] = capability
        registeredPlugins[capability.capabilityId] = executor
        IntentLogger.d("IntentFlowSdk", "Registered Capability: ${capability.displayName}")
    }

    fun registerKnowledgeProvider(provider: KnowledgeProvider<*>) {
        registeredKnowledgeProviders[provider.dataContract.providerId] = provider
        IntentLogger.d("IntentFlowSdk", "Registered Knowledge Provider: ${provider.dataContract.displayName}")
    }

    fun registerPlugin(plugin: AndroidPlugin) {
        registeredPlugins[plugin.pluginId] = plugin
        IntentLogger.d("IntentFlowSdk", "Registered Plugin: ${plugin.pluginId}")
    }

    fun registerExecutionProvider(provider: IntentExecutorProvider) {
        registeredProviders[provider.providerId] = provider
        IntentLogger.d("IntentFlowSdk", "Registered Execution Provider: ${provider.providerId}")
    }

    fun registerUiComponent(componentType: String, composable: Any) {
        registeredUiComponents[componentType] = composable
        IntentLogger.d("IntentFlowSdk", "Registered Dynamic UI Component: $componentType")
    }

    // Diagnostic queries
    fun getRegisteredDomainsCount(): Int = registeredDomains.size
    fun getRegisteredIntentsCount(): Int = registeredIntents.size
    fun getRegisteredSkillsCount(): Int = registeredSkills.size
}
