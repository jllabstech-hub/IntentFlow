package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Data Refresh Policy for Knowledge Providers.
 */
@Serializable
enum class KnowledgeRefreshPolicy {
    REALTIME,        // Scraped instantaneously on every query
    PERIODIC_5MIN,   // Refreshed every 5 minutes
    PERIODIC_1HOUR,  // Refreshed hourly
    ON_DEMAND,       // Scraped only when explicitly requested by an intent
    STATIC           // Evaluated once at app launch
}

/**
 * Metadata descriptor for a Knowledge Provider.
 */
@Serializable
data class KnowledgeDataContract(
    val providerId: String,
    val displayName: String,
    val dataType: String,
    val refreshPolicy: KnowledgeRefreshPolicy,
    val isSensitiveData: Boolean = false
)
