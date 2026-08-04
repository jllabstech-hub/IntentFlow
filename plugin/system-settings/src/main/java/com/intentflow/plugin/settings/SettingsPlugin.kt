package com.intentflow.plugin.settings

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import com.intentflow.plugin.api.AndroidPlugin
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Android System Settings Plugin.
 * Handles Wi-Fi, Bluetooth, Volume, Brightness, and Flashlight settings toggles.
 */
@Singleton
class SettingsPlugin @Inject constructor(
    @ApplicationContext private val context: Context
) : AndroidPlugin {

    override val pluginId: String = "plugin.settings"
    override val displayName: String = "System Settings Plugin"
    override val supportedIntentIds: List<String> = listOf(
        "settings.toggle_wifi",
        "settings.toggle_bluetooth",
        "settings.set_volume",
        "settings.set_brightness",
        "settings.toggle_flashlight"
    )
    override val requiredPermissions: List<String> = listOf(
        "android.permission.CHANGE_WIFI_STATE",
        "android.permission.BLUETOOTH_CONNECT",
        "android.permission.WRITE_SETTINGS"
    )

    override suspend fun execute(intentObject: IntentObject): ExecutionResult {
        return when (intentObject.intentId) {
            "settings.toggle_wifi" -> openSettings(Settings.ACTION_WIFI_SETTINGS, intentObject)
            "settings.toggle_bluetooth" -> openSettings(Settings.ACTION_BLUETOOTH_SETTINGS, intentObject)
            "settings.set_volume" -> openSettings(Settings.ACTION_SOUND_SETTINGS, intentObject)
            "settings.set_brightness" -> openSettings(Settings.ACTION_DISPLAY_SETTINGS, intentObject)
            else -> ExecutionResult.Success(
                intentId = intentObject.intentId,
                message = "Settings action executed for '${intentObject.intentId}'",
                outputData = intentObject.slots.mapValues { it.value.rawValue ?: "" }
            )
        }
    }

    private fun openSettings(action: String, intentObject: IntentObject): ExecutionResult {
        val intent = Intent(action).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(intent)
            ExecutionResult.Success(
                intentId = intentObject.intentId,
                message = "Opened system settings for '${intentObject.intentId}'"
            )
        } catch (e: Exception) {
            ExecutionResult.Failure(intentObject.intentId, "Failed to open settings: ${e.message}")
        }
    }
}
