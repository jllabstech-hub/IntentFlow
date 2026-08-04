package com.intentflow.engine.planner

import com.intentflow.core.model.CapabilityExecutionPlan
import com.intentflow.core.model.CapabilityProviderType
import com.intentflow.core.model.ExecutionTargetCapability
import com.intentflow.core.model.IntentObject
import com.intentflow.plugin.api.CapabilityRegistry
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Capability Execution Planner Interface.
 */
interface CapabilityExecutionPlanner {
    fun registerCapabilityHandler(capability: ExecutionTargetCapability, handlerId: String)
    suspend fun resolveCapability(intentObject: IntentObject): CapabilityExecutionPlan
}

/**
 * Execution Planner implementation querying CapabilityRegistry for execution discovery.
 */
@Singleton
class DefaultCapabilityExecutionPlanner @Inject constructor(
    private val capabilityRegistry: CapabilityRegistry
) : CapabilityExecutionPlanner {

    override fun registerCapabilityHandler(capability: ExecutionTargetCapability, handlerId: String) {
        // Capabilities are registered directly via CapabilityRegistry
    }

    override suspend fun resolveCapability(intentObject: IntentObject): CapabilityExecutionPlan {
        val compatibleExecutors = capabilityRegistry.findExecutorsForIntent(intentObject.intentId)
        val selectedExecutor = compatibleExecutors.firstOrNull()

        val mapping = intentObject.metadata["executionMapping"]
        val deepLink = intentObject.metadata["deepLink"]

        val selectedCapability = when {
            deepLink != null -> ExecutionTargetCapability.SYSTEM_DEEP_LINK
            selectedExecutor?.descriptor?.providerType == CapabilityProviderType.DEEP_LINK -> ExecutionTargetCapability.SYSTEM_DEEP_LINK
            selectedExecutor?.descriptor?.providerType == CapabilityProviderType.REST_API -> ExecutionTargetCapability.REST_API
            selectedExecutor?.descriptor?.providerType == CapabilityProviderType.EXTENSION -> ExecutionTargetCapability.LOCAL_RUNTIME
            selectedExecutor?.descriptor?.providerType == CapabilityProviderType.AI_PROVIDER -> ExecutionTargetCapability.AI_PROVIDER
            else -> ExecutionTargetCapability.ANDROID_PLUGIN
        }

        val handlerId = mapping ?: deepLink ?: selectedExecutor?.descriptor?.capabilityId ?: "plugin.telephony"
        val requiresConfirmation = intentObject.domain in listOf("payments", "banking")
        val fallbackExecutor = compatibleExecutors.getOrNull(1)

        val fallbackCapability = when (fallbackExecutor?.descriptor?.providerType) {
            CapabilityProviderType.AI_PROVIDER -> ExecutionTargetCapability.AI_PROVIDER
            CapabilityProviderType.ANDROID_PLUGIN -> ExecutionTargetCapability.ANDROID_PLUGIN
            else -> null
        }

        return CapabilityExecutionPlan(
            intentId = intentObject.intentId,
            selectedCapability = selectedCapability,
            handlerId = handlerId,
            resolvedParameters = intentObject.slots.mapValues { it.value.rawValue ?: "" },
            requiresUserConfirmation = requiresConfirmation,
            confirmationMessage = if (requiresConfirmation) "Confirm operation for ${intentObject.intentId}?" else null,
            fallbackCapability = fallbackCapability
        )
    }
}
