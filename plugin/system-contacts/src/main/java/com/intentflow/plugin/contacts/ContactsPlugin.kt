package com.intentflow.plugin.contacts

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import com.intentflow.plugin.api.AndroidPlugin
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Android Contacts Plugin.
 * Handles contact searching, picking, and adding contacts via ContactsContract.
 */
@Singleton
class ContactsPlugin @Inject constructor(
    @ApplicationContext private val context: Context
) : AndroidPlugin {

    override val pluginId: String = "plugin.contacts"
    override val displayName: String = "Contacts Plugin"
    override val supportedIntentIds: List<String> = listOf(
        "contacts.search",
        "contacts.pick",
        "contacts.add"
    )
    override val requiredPermissions: List<String> = listOf("android.permission.READ_CONTACTS")

    override suspend fun execute(intentObject: IntentObject): ExecutionResult {
        val intent = Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(intent)
            ExecutionResult.Success(
                intentId = intentObject.intentId,
                message = "Opened Android Contacts app"
            )
        } catch (e: Exception) {
            ExecutionResult.Failure(intentObject.intentId, "Failed to open Contacts: ${e.message}")
        }
    }
}
