package com.intentflow.engine.context

import com.intentflow.core.common.dispatcher.DefaultDispatcherProvider
import com.intentflow.engine.context.provider.AppKnowledgeProvider
import com.intentflow.engine.context.provider.CalendarKnowledgeProvider
import com.intentflow.engine.context.provider.ClipboardKnowledgeProvider
import com.intentflow.engine.context.provider.ContactsKnowledgeProvider
import com.intentflow.engine.context.provider.DriveKnowledgeProvider
import com.intentflow.engine.context.provider.HealthKnowledgeProvider
import com.intentflow.engine.context.provider.HistoryKnowledgeProvider
import com.intentflow.engine.context.provider.LocationKnowledgeProvider
import com.intentflow.engine.context.provider.PhotosKnowledgeProvider
import com.intentflow.engine.context.provider.TimeKnowledgeData
import com.intentflow.engine.context.provider.TimeKnowledgeProvider
import com.intentflow.engine.context.provider.WeatherKnowledgeProvider
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ContextEngineTest {

    private val timeProvider: TimeKnowledgeProvider = mockk()
    private val contactProvider: ContactsKnowledgeProvider = mockk()
    private val clipboardProvider: ClipboardKnowledgeProvider = mockk()
    private val locationProvider: LocationKnowledgeProvider = mockk()
    private val appProvider: AppKnowledgeProvider = mockk()
    private val historyProvider: HistoryKnowledgeProvider = mockk()
    private val calendarProvider: CalendarKnowledgeProvider = mockk(relaxed = true)
    private val photosProvider: PhotosKnowledgeProvider = mockk(relaxed = true)
    private val weatherProvider: WeatherKnowledgeProvider = mockk(relaxed = true)
    private val healthProvider: HealthKnowledgeProvider = mockk(relaxed = true)
    private val driveProvider: DriveKnowledgeProvider = mockk(relaxed = true)

    private lateinit var contextEngine: DefaultContextEngine

    @Before
    fun setup() {
        coEvery { timeProvider.fetchKnowledge() } returns TimeKnowledgeData(
            timestamp = 1770000000000L,
            timeFormatted = "14:30",
            dateFormatted = "2026-08-04",
            dayOfWeek = "Tuesday"
        )
        coEvery { contactProvider.fetchKnowledge() } returns listOf("Alice", "Bob")
        coEvery { clipboardProvider.fetchKnowledge() } returns "Copied snippet"
        coEvery { locationProvider.fetchKnowledge() } returns com.intentflow.engine.context.provider.LocationKnowledgeData(37.7749, -122.4194, "San Francisco")
        coEvery { appProvider.fetchKnowledge() } returns listOf("WhatsApp", "Maps")
        coEvery { historyProvider.fetchKnowledge() } returns listOf("messaging.send")
        coEvery { weatherProvider.fetchKnowledge() } returns "Sunny 24C"
        coEvery { healthProvider.fetchKnowledge() } returns "8400 steps"

        contextEngine = DefaultContextEngine(
            timeProvider, contactProvider, clipboardProvider,
            locationProvider, appProvider, historyProvider,
            calendarProvider, photosProvider, weatherProvider,
            healthProvider, driveProvider, DefaultDispatcherProvider()
        )
    }

    @Test
    fun testGetContextSnapshotAggregatesAllSignalsOnDevice() = runBlocking {
        val snapshot = contextEngine.getContextSnapshot()

        assertNotNull(snapshot)
        assertEquals("14:30", snapshot.currentTimeString)
        assertEquals("2026-08-04", snapshot.currentDateString)
        assertEquals(37.7749, snapshot.latitude!!, 0.001)
        assertEquals("San Francisco", snapshot.locationName)
        assertEquals("Copied snippet", snapshot.clipboardText)
        assertEquals(2, snapshot.recentContacts.size)
        assertEquals(2, snapshot.installedApps.size)
        assertEquals("messaging.send", snapshot.recentIntentIds.first())
        assertTrue(snapshot.userPreferences.containsKey("weather"))
    }
}
