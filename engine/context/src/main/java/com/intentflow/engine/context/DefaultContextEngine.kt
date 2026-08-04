package com.intentflow.engine.context

import com.intentflow.core.common.dispatcher.DispatcherProvider
import com.intentflow.core.model.ContextSnapshot
import com.intentflow.engine.context.provider.AppKnowledgeProvider
import com.intentflow.engine.context.provider.CalendarKnowledgeProvider
import com.intentflow.engine.context.provider.ClipboardKnowledgeProvider
import com.intentflow.engine.context.provider.ContactsKnowledgeProvider
import com.intentflow.engine.context.provider.DriveKnowledgeProvider
import com.intentflow.engine.context.provider.HealthKnowledgeProvider
import com.intentflow.engine.context.provider.HistoryKnowledgeProvider
import com.intentflow.engine.context.provider.LocationKnowledgeProvider
import com.intentflow.engine.context.provider.PhotosKnowledgeProvider
import com.intentflow.engine.context.provider.TimeKnowledgeProvider
import com.intentflow.engine.context.provider.WeatherKnowledgeProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultContextEngine @Inject constructor(
    private val timeProvider: TimeKnowledgeProvider,
    private val contactProvider: ContactsKnowledgeProvider,
    private val clipboardProvider: ClipboardKnowledgeProvider,
    private val locationProvider: LocationKnowledgeProvider,
    private val appProvider: AppKnowledgeProvider,
    private val historyProvider: HistoryKnowledgeProvider,
    private val calendarProvider: CalendarKnowledgeProvider,
    private val photosProvider: PhotosKnowledgeProvider,
    private val weatherProvider: WeatherKnowledgeProvider,
    private val healthProvider: HealthKnowledgeProvider,
    private val driveProvider: DriveKnowledgeProvider,
    private val dispatchers: DispatcherProvider
) : ContextEngine {

    override suspend fun getContextSnapshot(): ContextSnapshot = withContext(dispatchers.io) {
        val timeData = timeProvider.fetchKnowledge()
        val locData = locationProvider.fetchKnowledge()
        val contactsData = contactProvider.fetchKnowledge()
        val clipboardData = clipboardProvider.fetchKnowledge()
        val appsData = appProvider.fetchKnowledge()
        val historyData = historyProvider.fetchKnowledge()
        val weatherData = weatherProvider.fetchKnowledge()
        val healthData = healthProvider.fetchKnowledge()

        ContextSnapshot(
            timestamp = timeData.timestamp,
            currentTimeString = timeData.timeFormatted,
            currentDateString = timeData.dateFormatted,
            latitude = locData.latitude,
            longitude = locData.longitude,
            locationName = locData.locality,
            recentContacts = contactsData,
            installedApps = appsData,
            clipboardText = clipboardData,
            recentIntentIds = historyData,
            userPreferences = mapOf("weather" to weatherData, "health" to healthData)
        )
    }

    override fun updateClipboardContext(text: String) {}
    override fun recordIntentExecuted(intentId: String, slots: Map<String, String>) {}
}
