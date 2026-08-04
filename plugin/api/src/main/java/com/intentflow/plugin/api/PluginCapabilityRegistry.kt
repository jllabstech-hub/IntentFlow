package com.intentflow.plugin.api

import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Capability Registry indexing all installed Android Capability Plugins.
 * Resolves plugins dynamically by intent ID and executes system actions.
 * Thread-safe via [ConcurrentHashMap].
 */
@Singleton
class PluginCapabilityRegistry @Inject constructor(
    private val plugins: Set<@JvmSuppressWildcards AndroidPlugin>
) {
    private val pluginMap = ConcurrentHashMap<String, AndroidPlugin>()
    private val intentToPluginMap = ConcurrentHashMap<String, AndroidPlugin>()

    init {
        plugins.forEach { registerPlugin(it) }
    }

    fun registerPlugin(plugin: AndroidPlugin) {
        pluginMap[plugin.pluginId] = plugin
        for (intentId in plugin.supportedIntentIds) {
            intentToPluginMap[intentId] = plugin
        }
    }

    fun getPluginForIntent(intentId: String): AndroidPlugin? {
        return intentToPluginMap[intentId]
    }

    fun getAllPlugins(): List<AndroidPlugin> {
        return pluginMap.values.toList()
    }

    suspend fun executeIntent(intentObject: IntentObject): ExecutionResult {
        val plugin = getPluginForIntent(intentObject.intentId)
            ?: return ExecutionResult.Failure(
                intentId = intentObject.intentId,
                errorMessage = "No Android plugin registered to handle intent '${intentObject.intentId}'",
                cause = "PLUGIN_NOT_FOUND"
            )

        return plugin.execute(intentObject)
    }
}
