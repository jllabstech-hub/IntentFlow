package com.intentflow.engine.search

import kotlin.math.max
import kotlin.math.min

/**
 * String similarity algorithms for fuzzy matching, token overlap, and Levenshtein distance calculation.
 */
object FuzzyMatcher {

    /**
     * Calculates normalized Levenshtein similarity between 0.0 (completely different) and 1.0 (identical).
     */
    fun calculateLevenshteinSimilarity(s1: String, s2: String): Float {
        val str1 = s1.lowercase().trim()
        val str2 = s2.lowercase().trim()

        if (str1 == str2) return 1.0f
        if (str1.isEmpty() || str2.isEmpty()) return 0.0f

        val distance = computeLevenshteinDistance(str1, str2)
        val maxLength = max(str1.length, str2.length)
        return 1.0f - (distance.toFloat() / maxLength.toFloat())
    }

    /**
     * Calculates token overlap (Jaccard Index) between two sentences.
     */
    fun calculateTokenJaccardSimilarity(s1: String, s2: String): Float {
        val tokens1 = s1.lowercase().split("\\s+".toRegex()).filter { it.isNotBlank() }.toSet()
        val tokens2 = s2.lowercase().split("\\s+".toRegex()).filter { it.isNotBlank() }.toSet()

        if (tokens1.isEmpty() || tokens2.isEmpty()) return 0.0f

        val intersection = tokens1.intersect(tokens2).size
        val union = tokens1.union(tokens2).size

        return intersection.toFloat() / union.toFloat()
    }

    /**
     * Computes raw Levenshtein edit distance.
     */
    private fun computeLevenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }

        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = min(
                    min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[s1.length][s2.length]
    }
}
