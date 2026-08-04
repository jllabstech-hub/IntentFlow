package com.intentflow.engine.context.provider

import com.intentflow.core.model.KnowledgeDataContract
import com.intentflow.core.model.KnowledgeRefreshPolicy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactsKnowledgeProvider @Inject constructor() : KnowledgeProvider<List<String>> {
    override val dataContract = KnowledgeDataContract(
        providerId = "knowledge.contacts",
        displayName = "Contacts Knowledge",
        dataType = "List<String>",
        refreshPolicy = KnowledgeRefreshPolicy.PERIODIC_1HOUR,
        isSensitiveData = true
    )
    override val requiredPermissions = listOf("android.permission.READ_CONTACTS")
    override val refreshPolicy = KnowledgeRefreshPolicy.PERIODIC_1HOUR

    override fun isAvailable(): Boolean = true
    override suspend fun fetchKnowledge(): List<String> = listOf("Alice", "Bob", "Mom")
}

@Singleton
class ClipboardKnowledgeProvider @Inject constructor() : KnowledgeProvider<String> {
    override val dataContract = KnowledgeDataContract(
        providerId = "knowledge.clipboard",
        displayName = "Clipboard Knowledge",
        dataType = "String",
        refreshPolicy = KnowledgeRefreshPolicy.REALTIME
    )
    override val requiredPermissions = emptyList<String>()
    override val refreshPolicy = KnowledgeRefreshPolicy.REALTIME

    override fun isAvailable(): Boolean = true
    override suspend fun fetchKnowledge(): String? = "Copied text snippet"
}

@Singleton
class CalendarKnowledgeProvider @Inject constructor() : KnowledgeProvider<List<String>> {
    override val dataContract = KnowledgeDataContract(
        providerId = "knowledge.calendar",
        displayName = "Calendar Agenda Knowledge",
        dataType = "List<String>",
        refreshPolicy = KnowledgeRefreshPolicy.PERIODIC_1HOUR
    )
    override val requiredPermissions = listOf("android.permission.READ_CALENDAR")
    override val refreshPolicy = KnowledgeRefreshPolicy.PERIODIC_1HOUR

    override fun isAvailable(): Boolean = true
    override suspend fun fetchKnowledge(): List<String> = listOf("Team Sync at 15:00")
}

@Singleton
class AppKnowledgeProvider @Inject constructor() : KnowledgeProvider<List<String>> {
    override val dataContract = KnowledgeDataContract(
        providerId = "knowledge.apps",
        displayName = "Installed Apps Knowledge",
        dataType = "List<String>",
        refreshPolicy = KnowledgeRefreshPolicy.STATIC
    )
    override val requiredPermissions = emptyList<String>()
    override val refreshPolicy = KnowledgeRefreshPolicy.STATIC

    override fun isAvailable(): Boolean = true
    override suspend fun fetchKnowledge(): List<String> = listOf("WhatsApp", "Maps", "Settings")
}
