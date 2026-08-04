package com.intentflow.provider.gemma

import kotlinx.serialization.Serializable

/**
 * State representing Google AI Edge Gemma Model download status.
 */
sealed class ModelDownloadState {
    data object NotDownloaded : ModelDownloadState()

    data class Downloading(
        val progressPercent: Int,
        val bytesDownloaded: Long,
        val totalBytes: Long
    ) : ModelDownloadState()

    data class Paused(
        val progressPercent: Int,
        val bytesDownloaded: Long,
        val totalBytes: Long
    ) : ModelDownloadState()

    data class Downloaded(
        val modelPath: String,
        val fileSizeMb: Float,
        val checksum: String
    ) : ModelDownloadState()

    data class Error(
        val message: String
    ) : ModelDownloadState()
}
