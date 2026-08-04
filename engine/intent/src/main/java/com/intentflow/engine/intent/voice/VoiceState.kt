package com.intentflow.engine.intent.voice

import kotlinx.serialization.Serializable

/**
 * Reactive state machine for the Voice / Speech Engine.
 */
@Serializable
sealed class VoiceState {
    @Serializable
    data object Idle : VoiceState()

    @Serializable
    data object Listening : VoiceState()

    @Serializable
    data class SpeechRecognized(
        val spokenText: String,
        val confidence: Float = 1.0f
    ) : VoiceState()

    @Serializable
    data class Error(
        val message: String
    ) : VoiceState()
}
