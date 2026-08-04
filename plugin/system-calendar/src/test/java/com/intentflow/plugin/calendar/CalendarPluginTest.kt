package com.intentflow.plugin.calendar

import android.content.Context
import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.SlotValue
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CalendarPluginTest {

    private val context: Context = mockk(relaxed = true)
    private lateinit var calendarPlugin: CalendarPlugin

    @Before
    fun setup() {
        calendarPlugin = CalendarPlugin(context)
    }

    @Test
    fun testCreateEventExecution() = runBlocking {
        val intentObject = IntentObject(
            id = "1",
            intentId = "calendar.create_event",
            domain = "calendar",
            slots = mapOf(
                "title" to SlotValue("Team Sync", "Team Sync"),
                "date" to SlotValue("tomorrow", "tomorrow")
            )
        )

        val result = calendarPlugin.execute(intentObject)
        assertTrue(result is ExecutionResult.Success)
        assertEquals("calendar.create_event", (result as ExecutionResult.Success).intentId)
    }
}
