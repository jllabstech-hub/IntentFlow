package com.intentflow.engine.intent.runtime

import com.intentflow.core.model.ExecutionPlan
import com.intentflow.core.model.ExecutionTarget
import com.intentflow.core.model.IntentObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Builds an ExecutionPlan for a completed IntentObject.
 */
@Singleton
class ExecutionPlanner @Inject constructor() {

    fun buildExecutionPlan(intentObject: IntentObject): ExecutionPlan {
        val mapping = intentObject.metadata["executionMapping"]
        val resolvedParams = intentObject.slots.mapValues { it.value.rawValue ?: "" }

        val target = when {
            mapping?.startsWith("plugin.") == true -> ExecutionTarget.ANDROID_PLUGIN
            intentObject.metadata["deepLink"]?.startsWith("intentflow://") == true -> ExecutionTarget.SYSTEM_DEEP_LINK
            else -> ExecutionTarget.AI_PROVIDER
        }

        val targetHandler = mapping ?: intentObject.metadata["deepLink"] ?: "provider.mock"

        val requiresConfirmation = intentObject.domain in listOf("payments", "banking")

        return ExecutionPlan(
            intentId = intentObject.intentId,
            target = target,
            targetHandlerId = targetHandler,
            resolvedParameters = resolvedParams,
            requiresUserConfirmation = requiresConfirmation,
            confirmationMessage = if (requiresConfirmation) "Confirm action for ${intentObject.intentId}?" else null
        )
    }
}
