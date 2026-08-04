package com.intentflow.engine.intent.event

import com.intentflow.core.model.PipelineEvent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class PipelineEventBusTest {

    private lateinit var eventBus: PipelineEventBus

    @Before
    fun setup() {
        eventBus = DefaultPipelineEventBus()
    }

    @Test
    fun testPublishAndObservePipelineEvents() = runBlocking {
        val event = PipelineEvent.InputReceived(eventId = "evt_1", rawInput = "Set alarm for 7 AM")
        eventBus.publish(event)

        val received = eventBus.events.first()

        assertNotNull(received)
        assertEquals("evt_1", received.eventId)
        assertEquals("Set alarm for 7 AM", (received as PipelineEvent.InputReceived).rawInput)
    }
}
