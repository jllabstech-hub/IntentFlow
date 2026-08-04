package com.intentflow.engine.context

import com.intentflow.core.model.KnowledgeRefreshPolicy
import com.intentflow.engine.context.provider.CalendarKnowledgeProvider
import com.intentflow.engine.context.provider.DriveKnowledgeProvider
import com.intentflow.engine.context.provider.HealthKnowledgeProvider
import com.intentflow.engine.context.provider.PhotosKnowledgeProvider
import com.intentflow.engine.context.provider.TimeKnowledgeProvider
import com.intentflow.engine.context.provider.WeatherKnowledgeProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class KnowledgeProviderTest {

    @Test
    fun testTimeKnowledgeProviderContracts() = runBlocking {
        val provider = TimeKnowledgeProvider()

        assertTrue(provider.isAvailable())
        assertEquals(KnowledgeRefreshPolicy.REALTIME, provider.refreshPolicy)

        val data = provider.fetchKnowledge()
        assertNotNull(data)
        assertTrue(data.timestamp > 0)
    }

    @Test
    fun testExtendedKnowledgeProvidersContracts() = runBlocking {
        val calendar = CalendarKnowledgeProvider()
        val photos = PhotosKnowledgeProvider()
        val weather = WeatherKnowledgeProvider()
        val health = HealthKnowledgeProvider()
        val drive = DriveKnowledgeProvider()

        assertEquals(KnowledgeRefreshPolicy.PERIODIC_1HOUR, calendar.refreshPolicy)
        assertEquals(KnowledgeRefreshPolicy.ON_DEMAND, photos.refreshPolicy)
        assertEquals(KnowledgeRefreshPolicy.PERIODIC_5MIN, weather.refreshPolicy)
        assertEquals(KnowledgeRefreshPolicy.PERIODIC_1HOUR, health.refreshPolicy)
        assertEquals(KnowledgeRefreshPolicy.ON_DEMAND, drive.refreshPolicy)

        assertNotNull(health.fetchKnowledge())
        assertNotNull(weather.fetchKnowledge())
    }
}
