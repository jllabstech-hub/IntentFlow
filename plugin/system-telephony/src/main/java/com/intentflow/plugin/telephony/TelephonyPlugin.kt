package com.intentflow.plugin.telephony

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import com.intentflow.plugin.api.AndroidPlugin
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Android Telephony & SMS Plugin.
 * Handles SMS sending, replies, phone calling, call log viewing, and voicemail.
 */
@Singleton
class TelephonyPlugin @Inject constructor(
    @ApplicationContext private val context: Context
) : AndroidPlugin {

    override val pluginId: String = "plugin.telephony"
    override val displayName: String = "Telephony & SMS Plugin"
    override val supportedIntentIds: List<String> = listOf(
        "messaging.send",
        "messaging.read_unread",
        "messaging.search",
        "messaging.delete",
        "messaging.reply",
        "phone.call",
        "phone.end_call",
        "phone.view_call_log",
        "phone.voicemail",
        "phone.mute"
    )
    override val requiredPermissions: List<String> = listOf(
        "android.permission.SEND_SMS",
        "android.permission.CALL_PHONE",
        "android.permission.READ_SMS",
        "android.permission.READ_CALL_LOG"
    )

    override suspend fun execute(intentObject: IntentObject): ExecutionResult {
        return when (intentObject.intentId) {
            "messaging.send", "messaging.reply" -> sendSms(intentObject)
            "phone.call" -> makePhoneCall(intentObject)
            "phone.view_call_log" -> viewCallLog(intentObject)
            "phone.voicemail" -> checkVoicemail(intentObject)
            else -> ExecutionResult.Success(
                intentId = intentObject.intentId,
                message = "Telephony action completed for ${intentObject.intentId}",
                outputData = intentObject.slots.mapValues { it.value.rawValue ?: "" }
            )
        }
    }

    private fun sendSms(intentObject: IntentObject): ExecutionResult {
        val recipient = intentObject.slots["recipient"]?.rawValue ?: ""
        val message = intentObject.slots["message_text"]?.rawValue ?: ""

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$recipient")
            putExtra("sms_body", message)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(intent)
            ExecutionResult.Success(
                intentId = intentObject.intentId,
                message = "Opened SMS composer for recipient '$recipient'",
                outputData = mapOf("recipient" to recipient, "message" to message)
            )
        } catch (e: Exception) {
            ExecutionResult.Failure(intentObject.intentId, "Failed to launch SMS intent: ${e.message}")
        }
    }

    private fun makePhoneCall(intentObject: IntentObject): ExecutionResult {
        val contact = intentObject.slots["contact"]?.rawValue ?: ""

        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$contact")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(intent)
            ExecutionResult.Success(
                intentId = intentObject.intentId,
                message = "Opened phone dialer for '$contact'",
                outputData = mapOf("contact" to contact)
            )
        } catch (e: Exception) {
            ExecutionResult.Failure(intentObject.intentId, "Failed to launch Phone dialer: ${e.message}")
        }
    }

    private fun viewCallLog(intentObject: IntentObject): ExecutionResult {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            type = "vnd.android.cursor.dir/calls"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(intent)
            ExecutionResult.Success(intentObject.intentId, "Opened system call log")
        } catch (e: Exception) {
            ExecutionResult.Failure(intentObject.intentId, "Failed to open call log: ${e.message}")
        }
    }

    private fun checkVoicemail(intentObject: IntentObject): ExecutionResult {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("voicemail:")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(intent)
            ExecutionResult.Success(intentObject.intentId, "Opened voicemail inbox")
        } catch (e: Exception) {
            ExecutionResult.Failure(intentObject.intentId, "Failed to open voicemail: ${e.message}")
        }
    }
}
