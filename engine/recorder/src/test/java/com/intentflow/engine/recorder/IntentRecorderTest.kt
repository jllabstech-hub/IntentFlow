package com.intentflow.engine.recorder

import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.RecordedInteractionTrace
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class IntentRecorderTest {

    private lateinit var recorder: IntentRecorder
    private lateinit var replayer: TraceReplayer

    @Before
    fun setup() {
        recorder = DefaultIntentRecorder()
        replayer = DefaultTraceReplayer(recorder)
    }

    @Test
    fun testRecordAndRetrieveInteractionTrace() = runBlocking {
        val trace = RecordedInteractionTrace(
            traceId = "trc_1",
            rawInput = "Turn on Wi-Fi",
            intentObject = IntentObject("1", "settings.toggle_wifi", "settings"),
            executionLatencyMs = 12L
        )

        recorder.recordInteraction(trace)

        val retrieved = recorder.getTraceById("trc_1")
        assertNotNull(retrieved)
        assertEquals("Turn on Wi-Fi", retrieved?.rawInput)

        val traces = recorder.listTraces()
        assertEquals(1, traces.size)
    }

    @Test
    fun testReplayTraceFlow() = runBlocking {
        val trace = RecordedInteractionTrace(
            traceId = "trc_2",
            rawInput = "Call Mom",
            intentObject = IntentObject("2", "phone.call", "telephony"),
            providerResponse = "Phone call dialed"
        )

        recorder.recordInteraction(trace)

        val firstState = replayer.replayTrace("trc_2").first()
        assertNotNull(firstState)
        assertTrue(firstState is com.intentflow.core.model.IntentState.ProcessingInput)
    }
}
