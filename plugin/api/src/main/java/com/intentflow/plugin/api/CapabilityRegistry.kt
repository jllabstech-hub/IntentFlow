package com.intentflow.plugin.api

import com.intentflow.core.model.CapabilityDescriptor
import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface implemented by any executor capability (Plugin, AI Provider, REST Endpoint, Deep Link, Extension).
 */
interface CapabilityExecutor {
    val descriptor: CapabilityDescriptor

    suspend fun execute(intentObject: IntentObject): ExecutionResult
}

/**
 * Unified Capability Registry interface acting as the central execution discovery engine.
 */
interface CapabilityRegistry {
    fun registerCapability(executor: CapabilityExecutor)
    fun unregisterCapability(capabilityId: String)
    fun findExecutorsForIntent(intentId: String): List<CapabilityExecutor>
    fun findOfflineExecutorsForIntent(intentId: String): List<CapabilityExecutor>
    fun getAllCapabilities(): List<CapabilityDescriptor>
}

/**
 * Production-ready thread-safe implementation of CapabilityRegistry.
 */
@Singleton
class DefaultCapabilityRegistry @Inject constructor(
    executors: Set<@JvmSuppressWildcards CapabilityExecutor>
) : CapabilityRegistry {

    private val capabilityMap = ConcurrentHashMap<String, CapabilityExecutor>()
    private val intentToCapabilityMap = ConcurrentHashMap<String, ConcurrentHashMap<String, CapabilityExecutor>>()

    init {
        executors.forEach { registerCapability(it) }
    }

    override fun registerCapability(executor: CapabilityExecutor) {
        val desc = executor.descriptor
        capabilityMap[desc.capabilityId] = executor

        for (intentId in desc.supportedIntentIds) {
            val mapForIntent = intentToCapabilityMap.getOrPut(intentId) { ConcurrentHashMap() }
            mapForIntent[desc.capabilityId] = executor
        }
    }

    override fun unregisterCapability(capabilityId: String) {
        val executor = capabilityMap.remove(capabilityId) ?: return
        for (intentId in executor.descriptor.supportedIntentIds) {
            intentToCapabilityMap[intentId]?.remove(capabilityId)
        }
    }

    override fun findExecutorsForIntent(intentId: String): List<CapabilityExecutor> {
        val matches = intentToCapabilityMap[intentId]?.values?.toList() ?: emptyList()
        return matches.sortedByDescending { it.descriptor.priority }
    }

    override fun findOfflineExecutorsForIntent(intentId: String): List<CapabilityExecutor> {
        return findExecutorsForIntent(intentId).filter { it.descriptor.isOfflineCapable }
    }

    override fun getAllCapabilities(): List<CapabilityDescriptor> {
        return capabilityMap.values.map { it.descriptor }
    }
}
