package com.intentflow.engine.context.provider

import com.intentflow.core.model.KnowledgeDataContract
import com.intentflow.core.model.KnowledgeRefreshPolicy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryKnowledgeProvider @Inject constructor() : KnowledgeProvider<List<String>> {
    override val dataContract = KnowledgeDataContract(
        providerId = "knowledge.history",
        displayName = "Intent History Knowledge",
        dataType = "List<String>",
        refreshPolicy = KnowledgeRefreshPolicy.REALTIME
    )
    override val requiredPermissions = emptyList<String>()
    override val refreshPolicy = KnowledgeRefreshPolicy.REALTIME

    override fun isAvailable(): Boolean = true
    override suspend fun fetchKnowledge(): List<String> = listOf("messaging.send", "alarm.set")
}

@Singleton
class PhotosKnowledgeProvider @Inject constructor() : KnowledgeProvider<String> {
    override val dataContract = KnowledgeDataContract(
        providerId = "knowledge.photos",
        displayName = "Photos Library Knowledge",
        dataType = "String",
        refreshPolicy = KnowledgeRefreshPolicy.ON_DEMAND,
        isSensitiveData = true
    )
    override val requiredPermissions = listOf("android.permission.READ_MEDIA_IMAGES")
    override val refreshPolicy = KnowledgeRefreshPolicy.ON_DEMAND

    override fun isAvailable(): Boolean = true
    override suspend fun fetchKnowledge(): String = "Latest photo: IMG_2026.jpg"
}

@Singleton
class WeatherKnowledgeProvider @Inject constructor() : KnowledgeProvider<String> {
    override val dataContract = KnowledgeDataContract(
        providerId = "knowledge.weather",
        displayName = "Local Weather Knowledge",
        dataType = "String",
        refreshPolicy = KnowledgeRefreshPolicy.PERIODIC_5MIN
    )
    override val requiredPermissions = listOf("android.permission.ACCESS_COARSE_LOCATION")
    override val refreshPolicy = KnowledgeRefreshPolicy.PERIODIC_5MIN

    override fun isAvailable(): Boolean = true
    override suspend fun fetchKnowledge(): String = "72°F Sunny"
}

@Singleton
class HealthKnowledgeProvider @Inject constructor() : KnowledgeProvider<String> {
    override val dataContract = KnowledgeDataContract(
        providerId = "knowledge.health",
        displayName = "Samsung Health Knowledge",
        dataType = "String",
        refreshPolicy = KnowledgeRefreshPolicy.PERIODIC_1HOUR,
        isSensitiveData = true
    )
    override val requiredPermissions = listOf("android.permission.BODY_SENSORS")
    override val refreshPolicy = KnowledgeRefreshPolicy.PERIODIC_1HOUR

    override fun isAvailable(): Boolean = true
    override suspend fun fetchKnowledge(): String = "Steps: 8,420 | Heart Rate: 72 bpm"
}

@Singleton
class DriveKnowledgeProvider @Inject constructor() : KnowledgeProvider<List<String>> {
    override val dataContract = KnowledgeDataContract(
        providerId = "knowledge.drive",
        displayName = "Google Drive Knowledge",
        dataType = "List<String>",
        refreshPolicy = KnowledgeRefreshPolicy.ON_DEMAND
    )
    override val requiredPermissions = emptyList<String>()
    override val refreshPolicy = KnowledgeRefreshPolicy.ON_DEMAND

    override fun isAvailable(): Boolean = true
    override suspend fun fetchKnowledge(): List<String> = listOf("Project_Plan.pdf", "Budget.xlsx")
}
