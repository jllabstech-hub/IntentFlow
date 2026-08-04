package com.intentflow.engine.context.provider

import android.content.ClipboardManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * On-device Provider for Clipboard Text.
 */
@Singleton
class ClipboardContextProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getClipboardText(): String? {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            if (clipboard != null && clipboard.hasPrimaryClip()) {
                val item = clipboard.primaryClip?.getItemAt(0)
                val text = item?.text?.toString()
                if (!text.isNullOrBlank()) {
                    return text.trim()
                }
            }
        } catch (e: Exception) {
            // Ignore clipboard errors safely
        }
        return null
    }
}
