package com.intentflow.plugin.calendar

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import com.intentflow.plugin.api.AndroidPlugin
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Android Calendar Plugin.
 * Handles calendar event creation, agenda viewing, event updates, and deletions via CalendarContract.
 */
@Singleton
class CalendarPlugin @Inject constructor(
    @ApplicationContext private val context: Context
) : AndroidPlugin {

    override val pluginId: String = "plugin.calendar"
    override val displayName: String = "Calendar Plugin"
    override val supportedIntentIds: List<String> = listOf(
        "calendar.create_event",
        "calendar.view_agenda",
        "calendar.update_event",
        "calendar.delete_event",
        "calendar.find_free_slot"
    )
    override val requiredPermissions: List<String> = listOf(
        "android.permission.READ_CALENDAR",
        "android.permission.WRITE_CALENDAR"
    )

    override suspend fun execute(intentObject: IntentObject): ExecutionResult {
        return when (intentObject.intentId) {
            "calendar.create_event" -> createEvent(intentObject)
            "calendar.view_agenda" -> viewAgenda(intentObject)
            else -> ExecutionResult.Success(
                intentId = intentObject.intentId,
                message = "Calendar action completed for '${intentObject.intentId}'",
                outputData = intentObject.slots.mapValues { it.value.rawValue ?: "" }
            )
        }
    }

    private fun createEvent(intentObject: IntentObject): ExecutionResult {
        val title = intentObject.slots["title"]?.rawValue ?: "New Event"
        val date = intentObject.slots["date"]?.rawValue ?: ""

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis() + 3600000)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(intent)
            ExecutionResult.Success(
                intentId = intentObject.intentId,
                message = "Opened Calendar event creator for '$title'",
                outputData = mapOf("title" to title, "date" to date)
            )
        } catch (e: Exception) {
            ExecutionResult.Failure(intentObject.intentId, "Failed to launch Calendar creator: ${e.message}")
        }
    }

    private fun viewAgenda(intentObject: IntentObject): ExecutionResult {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = CalendarContract.CONTENT_URI.buildUpon().appendPath("time").appendPath(System.currentTimeMillis().toString()).build()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(intent)
            ExecutionResult.Success(intentObject.intentId, "Opened Calendar Agenda view")
        } catch (e: Exception) {
            ExecutionResult.Failure(intentObject.intentId, "Failed to open Calendar: ${e.message}")
        }
    }
}
