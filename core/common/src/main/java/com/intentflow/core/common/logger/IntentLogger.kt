package com.intentflow.core.common.logger

import timber.log.Timber

/**
 * IntentFlow platform logging facade.
 *
 * Wraps [Timber] to provide a consistent logging API across all modules.
 * All calls are no-ops in release builds (Timber has no tree planted).
 *
 * Usage:
 * ```kotlin
 * IntentLogger.d("MyTag", "Processing intent")
 * IntentLogger.e("CatalogRepo", "Failed to load catalog", exception)
 * ```
 */
object IntentLogger {

    /** Logs a debug message. */
    fun d(tag: String, message: String) {
        Timber.tag(tag).d(message)
    }

    /** Logs an informational message. */
    fun i(tag: String, message: String) {
        Timber.tag(tag).i(message)
    }

    /** Logs a warning, optionally with a [throwable]. */
    fun w(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Timber.tag(tag).w(throwable, message)
        } else {
            Timber.tag(tag).w(message)
        }
    }

    /** Logs an error with an optional [throwable]. */
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Timber.tag(tag).e(throwable, message)
        } else {
            Timber.tag(tag).e(message)
        }
    }

    /** Logs a verbose message — only in debug builds. */
    fun v(tag: String, message: String) {
        Timber.tag(tag).v(message)
    }
}
