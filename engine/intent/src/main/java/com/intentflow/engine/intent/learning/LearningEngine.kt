package com.intentflow.engine.intent.learning

import com.intentflow.core.model.UserBehaviorHistory
import com.intentflow.engine.search.ScoredIntentMatch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 100% On-Device Personalization & Learning Engine.
 * Adjusts candidate intent ranking over time based on local frequency, recency, and preferred slot values.
 * Zero cloud upload.
 */
@Singleton
class LearningEngine @Inject constructor() {

    private val intentUsageCounts = ConcurrentHashMap<String, Int>()
    private val recentIntents = CopyOnWriteArrayList<String>()
    private val lastUsedTimestamps = ConcurrentHashMap<String, Long>()
    private val slotValueFrequency = ConcurrentHashMap<String, ConcurrentHashMap<String, Int>>()

    fun recordIntentUsage(intentId: String, slots: Map<String, String> = emptyMap()) {
        val now = System.currentTimeMillis()

        // 1. Update frequency
        intentUsageCounts[intentId] = (intentUsageCounts[intentId] ?: 0) + 1

        // 2. Update recency list
        recentIntents.remove(intentId)
        recentIntents.add(0, intentId)
        if (recentIntents.size > 50) {
            recentIntents.removeAt(recentIntents.size - 1)
        }

        // 3. Update last used timestamp
        lastUsedTimestamps[intentId] = now

        // 4. Update slot value preferences
        for ((slotName, value) in slots) {
            if (value.isNotBlank()) {
                val valueMap = slotValueFrequency.getOrPut(slotName) { ConcurrentHashMap() }
                valueMap[value] = (valueMap[value] ?: 0) + 1
            }
        }
    }

    fun personalizeRanking(candidateMatches: List<ScoredIntentMatch>): List<ScoredIntentMatch> {
        val now = System.currentTimeMillis()
        val totalUsageAllIntents = intentUsageCounts.values.sum().coerceAtLeast(1)

        val reRanked = candidateMatches.map { match ->
            val intentId = match.intent.intentId

            // Frequency boost (up to +0.15)
            val count = intentUsageCounts[intentId] ?: 0
            val frequencyBoost = (count.toFloat() / totalUsageAllIntents.toFloat()) * 0.15f

            // Recency boost (up to +0.10 for usage in last 24 hours)
            val lastUsed = lastUsedTimestamps[intentId] ?: 0L
            val hoursSinceUsed = if (lastUsed > 0) (now - lastUsed).toFloat() / (1000 * 3600) else 999f
            val recencyBoost = if (hoursSinceUsed <= 24f) {
                ((24f - hoursSinceUsed) / 24f) * 0.10f
            } else 0.0f

            val personalizedScore = (match.score + frequencyBoost + recencyBoost).coerceIn(0.0f, 1.0f)
            match.copy(score = personalizedScore)
        }

        return reRanked.sortedByDescending { it.score }
    }

    fun getUserBehaviorHistory(): UserBehaviorHistory {
        return UserBehaviorHistory(
            intentUsageCounts = intentUsageCounts.toMap(),
            recentIntents = recentIntents.toList(),
            slotValueFrequency = slotValueFrequency.mapValues { it.value.toMap() },
            lastUsedTimestamp = lastUsedTimestamps.toMap()
        )
    }

    fun clearHistory() {
        intentUsageCounts.clear()
        recentIntents.clear()
        lastUsedTimestamps.clear()
        slotValueFrequency.clear()
    }
}
