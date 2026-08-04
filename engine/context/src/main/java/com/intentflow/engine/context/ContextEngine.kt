package com.intentflow.engine.context

import com.intentflow.core.model.ContextSnapshot

/**
 * Base interface for the On-Device Context Engine.
 * Scrapes system context (Time, Date, Installed Apps, Contacts, Clipboard) 100% locally.
 */
interface ContextEngine {
    suspend fun getContextSnapshot(): ContextSnapshot
    fun updateClipboardContext(text: String)
    fun recordIntentExecuted(intentId: String, slots: Map<String, String>)
}
