package com.intentflow.engine.intent.voice

import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentState
import com.intentflow.engine.intent.runtime.IntentRuntime
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class VoiceIntentEnginePipelineTest {

    private val speechProvider: SpeechRecognizerProvider = mockk()
    private val intentRuntime: IntentRuntime = mockk()
    private val voiceStateFlow = MutableStateFlow<VoiceState>(VoiceState.Idle)

    private lateinit var voicePipeline: VoiceIntentEnginePipeline

    @Before
    fun setup() {
        every { speechProvider.voiceState } returns voiceStateFlow
        every { intentRuntime.processNaturalLanguage("Set alarm for 7 AM") } returns flowOf(
            IntentState.Completed(ExecutionResult.Success("alarm.set", "Alarm set"))
        )

        voicePipeline = VoiceIntentEnginePipeline(speechProvider, intentRuntime)
    }

    @Test
    fun testVoiceStateTriggersSharedIntentRuntimePipeline() = runBlocking {
        voiceStateFlow.value = VoiceState.SpeechRecognized("Set alarm for 7 AM")

        val state = voicePipeline.observeVoiceToIntentPipeline().first()
        assertNotNull(state)
        assertEquals(IntentState.Completed(ExecutionResult.Success("alarm.set", "Alarm set")), state)
    }
}
