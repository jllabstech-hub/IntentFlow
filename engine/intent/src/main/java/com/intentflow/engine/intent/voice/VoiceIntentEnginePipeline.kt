package com.intentflow.engine.intent.voice

import com.intentflow.core.model.IntentState
import com.intentflow.engine.intent.runtime.IntentRuntime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Connects Speech Recognition to the Intent Runtime Pipeline.
 * Enforces the core principle: Typing and Voice share the exact same Intent Runtime pipeline.
 *
 * Voice → Speech Recognition → Intent Engine → Slot Filling → Dynamic UI → Result
 */
@Singleton
class VoiceIntentEnginePipeline @Inject constructor(
    val speechProvider: SpeechRecognizerProvider,
    private val intentRuntime: IntentRuntime
) {

    fun observeVoiceToIntentPipeline(): Flow<IntentState> {
        return speechProvider.voiceState
            .filterIsInstance<VoiceState.SpeechRecognized>()
            .flatMapLatest { recognized ->
                intentRuntime.processNaturalLanguage(recognized.spokenText)
            }
    }

    fun startVoiceInput() {
        speechProvider.startListening()
    }

    fun stopVoiceInput() {
        speechProvider.stopListening()
    }
}
