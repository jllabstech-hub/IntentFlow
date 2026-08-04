package com.intentflow.plugin.api

import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject

/**
 * Base interface for all Android Capability Plugins.
 * Defines capabilities, required permissions, intent support, and execution.
 */
interface AndroidPlugin {
    val pluginId: String
    val displayName: String
    val supportedIntentIds: List<String>
    val requiredPermissions: List<String>

    suspend fun execute(intentObject: IntentObject): ExecutionResult
}
