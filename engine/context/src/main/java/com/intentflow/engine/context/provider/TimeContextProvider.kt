package com.intentflow.engine.context.provider

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class TimeContextInfo(
    val timestamp: Long,
    val timeFormatted: String,
    val dateFormatted: String,
    val dayOfWeek: String
)

/**
 * On-device Provider for Current Date and Current Time.
 */
@Singleton
class TimeContextProvider @Inject constructor() {

    fun getCurrentTimeInfo(): TimeContextInfo {
        val now = System.currentTimeMillis()
        val date = Date(now)

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())

        return TimeContextInfo(
            timestamp = now,
            timeFormatted = timeFormat.format(date),
            dateFormatted = dateFormat.format(date),
            dayOfWeek = dayFormat.format(date)
        )
    }
}
