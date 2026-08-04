package com.intentflow.engine.context.provider

import com.intentflow.core.model.KnowledgeDataContract
import com.intentflow.core.model.KnowledgeRefreshPolicy
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class TimeKnowledgeData(
    val timestamp: Long = System.currentTimeMillis(),
    val timeFormatted: String,
    val dateFormatted: String,
    val dayOfWeek: String
)

@Singleton
class TimeKnowledgeProvider @Inject constructor() : KnowledgeProvider<TimeKnowledgeData> {
    override val dataContract = KnowledgeDataContract(
        providerId = "knowledge.time",
        displayName = "Time & Date Knowledge",
        dataType = "TimeKnowledgeData",
        refreshPolicy = KnowledgeRefreshPolicy.REALTIME
    )
    override val requiredPermissions: List<String> = emptyList()
    override val refreshPolicy: KnowledgeRefreshPolicy = KnowledgeRefreshPolicy.REALTIME

    override fun isAvailable(): Boolean = true

    override suspend fun fetchKnowledge(): TimeKnowledgeData {
        val now = Date()
        return TimeKnowledgeData(
            timestamp = now.time,
            timeFormatted = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now),
            dateFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now),
            dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(now)
        )
    }
}

data class LocationKnowledgeData(
    val latitude: Double,
    val longitude: Double,
    val locality: String
)

@Singleton
class LocationKnowledgeProvider @Inject constructor() : KnowledgeProvider<LocationKnowledgeData> {
    override val dataContract = KnowledgeDataContract(
        providerId = "knowledge.location",
        displayName = "Location Knowledge",
        dataType = "LocationKnowledgeData",
        refreshPolicy = KnowledgeRefreshPolicy.PERIODIC_5MIN,
        isSensitiveData = true
    )
    override val requiredPermissions = listOf("android.permission.ACCESS_FINE_LOCATION")
    override val refreshPolicy = KnowledgeRefreshPolicy.PERIODIC_5MIN

    override fun isAvailable(): Boolean = true

    override suspend fun fetchKnowledge(): LocationKnowledgeData {
        return LocationKnowledgeData(37.7749, -122.4194, "San Francisco")
    }
}
