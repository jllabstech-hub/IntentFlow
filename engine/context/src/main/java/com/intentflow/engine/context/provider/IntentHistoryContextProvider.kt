package com.intentflow.engine.context.provider

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

/**
 * On-device Provider for Recent Intents and Frequently Used Slot Values.
 */
@Singleton
class IntentHistoryContextProvider @Inject constructor() {

    private val recentIntentsList = CopyOnWriteArrayList<String>()
    private val valueFrequencyMap = ConcurrentHashMap<String, Int>()

    fun recordIntentExecution(intentId: String, slots: Map<String, String> = emptyMap()) {
        recentIntentsList.remove(intentId)
        recentIntentsList.add(0, intentId)
        if (recentIntentsList.size > 20) {
            recentIntentsList.removeAt(recentIntentsList.size - 1)
        }

        for (valEntry in slots.values) {
            if (valEntry.isNotBlank()) {
                valueFrequencyMap[valEntry] = (valueFrequencyMap[valEntry] ?: 0) + 1
            }
        }
    }

    fun getRecentIntents(limit: Int = 10): List<String> {
        return recentIntentsList.take(limit)
    }

    fun getFrequentlyUsedValues(limit: Int = 10): Map<String, String> {
        return valueFrequencyMap.entries
            .sortedByDescending { it.value }
            .take(limit)
            .associate { it.key to "Frequency: ${it.value}" }
    }
}
