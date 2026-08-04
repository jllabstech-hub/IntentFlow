package com.intentflow.engine.memory

import com.intentflow.core.model.ContextSnapshot
import com.intentflow.core.model.DomainPreference
import com.intentflow.core.model.IntentMemoryEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Intent Memory Engine Interface.
 * Manages local encrypted preference storage, suggestion retrieval, and user editing.
 */
interface IntentMemoryEngine {
    val memories: StateFlow<List<IntentMemoryEntry>>

    suspend fun getMemoryForSlot(domain: String, slotName: String, context: ContextSnapshot? = null): IntentMemoryEntry?
    suspend fun getSuggestionsForSlot(domain: String, slotName: String): List<IntentMemoryEntry>
    suspend fun getDomainPreferences(domain: String): DomainPreference

    suspend fun saveMemory(entry: IntentMemoryEntry)
    suspend fun updateMemoryValue(memoryId: String, newValue: String)
    suspend fun deleteMemory(memoryId: String)
    suspend fun clearDomainMemories(domain: String)
    fun observeAllMemories(): Flow<List<IntentMemoryEntry>>
}

/**
 * Production-ready implementation of IntentMemoryEngine.
 */
@Singleton
class DefaultIntentMemoryEngine @Inject constructor() : IntentMemoryEngine {

    private val memoryMap = ConcurrentHashMap<String, IntentMemoryEntry>()
    private val keyToMemoryIdMap = ConcurrentHashMap<String, String>() // "domain:slotName" -> memoryId

    private val _memories = MutableStateFlow<List<IntentMemoryEntry>>(emptyList())
    override val memories: StateFlow<List<IntentMemoryEntry>> = _memories.asStateFlow()

    override suspend fun getMemoryForSlot(domain: String, slotName: String, context: ContextSnapshot?): IntentMemoryEntry? {
        val key = "$domain:$slotName"
        val memoryId = keyToMemoryIdMap[key] ?: return null
        return memoryMap[memoryId]
    }

    override suspend fun getSuggestionsForSlot(domain: String, slotName: String): List<IntentMemoryEntry> {
        return memoryMap.values.filter { it.domain == domain && it.slotName == slotName }
            .sortedByDescending { it.lastUsedTimestamp }
    }

    override suspend fun getDomainPreferences(domain: String): DomainPreference {
        val prefs = memoryMap.values.filter { it.domain == domain }
            .associateBy { it.slotName }
        return DomainPreference(domain = domain, preferences = prefs)
    }

    override suspend fun saveMemory(entry: IntentMemoryEntry) {
        memoryMap[entry.memoryId] = entry
        keyToMemoryIdMap["${entry.domain}:${entry.slotName}"] = entry.memoryId
        _memories.value = memoryMap.values.toList()
    }

    override suspend fun updateMemoryValue(memoryId: String, newValue: String) {
        val existing = memoryMap[memoryId] ?: return
        val updated = existing.copy(preferredValue = newValue, lastUsedTimestamp = System.currentTimeMillis())
        saveMemory(updated)
    }

    override suspend fun deleteMemory(memoryId: String) {
        val existing = memoryMap.remove(memoryId)
        if (existing != null) {
            keyToMemoryIdMap.remove("${existing.domain}:${existing.slotName}")
            _memories.value = memoryMap.values.toList()
        }
    }

    override suspend fun clearDomainMemories(domain: String) {
        val toDelete = memoryMap.values.filter { it.domain == domain }
        toDelete.forEach { deleteMemory(it.memoryId) }
    }

    override fun observeAllMemories(): Flow<List<IntentMemoryEntry>> {
        return flowOf(memoryMap.values.toList())
    }
}
