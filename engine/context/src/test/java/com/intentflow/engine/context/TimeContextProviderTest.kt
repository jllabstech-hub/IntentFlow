package com.intentflow.engine.context

import com.intentflow.engine.context.provider.TimeContextProvider
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TimeContextProviderTest {

    private val provider = TimeContextProvider()

    @Test
    fun testTimeContextProviderReturnsValidFormat() {
        val info = provider.getCurrentTimeInfo()

        assertNotNull(info)
        assertTrue(info.timestamp > 0)
        assertTrue(info.timeFormatted.matches(Regex("^\\d{2}:\\d{2}\$")))
        assertTrue(info.dateFormatted.matches(Regex("^\\d{4}-\\d{2}-\\d{2}\$")))
        assertTrue(info.dayOfWeek.isNotBlank())
    }
}
