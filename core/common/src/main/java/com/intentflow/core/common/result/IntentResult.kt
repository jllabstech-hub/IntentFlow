package com.intentflow.core.common.result

/**
 * Universal Result wrapper for asynchronous operations in IntentFlow.
 */
sealed class IntentResult<out T> {
    data class Success<out T>(val data: T) : IntentResult<T>()
    data class Error(val exception: Throwable, val message: String? = exception.localizedMessage) : IntentResult<Nothing>()
    data object Loading : IntentResult<Nothing>()
}
