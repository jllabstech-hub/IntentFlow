package com.intentflow.engine.context.provider

import com.intentflow.core.model.KnowledgeDataContract
import com.intentflow.core.model.KnowledgeRefreshPolicy

/**
 * Standardized interface implemented by all 11 Knowledge Providers in IntentFlow.
 */
interface KnowledgeProvider<T> {
    val dataContract: KnowledgeDataContract
    val requiredPermissions: List<String>
    val refreshPolicy: KnowledgeRefreshPolicy

    fun isAvailable(): Boolean
    suspend fun fetchKnowledge(): T?
}
