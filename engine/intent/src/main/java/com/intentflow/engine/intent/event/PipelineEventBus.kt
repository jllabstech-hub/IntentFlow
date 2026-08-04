package com.intentflow.engine.intent.event

import com.intentflow.core.common.logger.IntentLogger
import com.intentflow.core.model.PipelineEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thread-safe Event Bus interface publishing and observing pipeline lifecycle events.
 */
interface PipelineEventBus {
    val events: SharedFlow<PipelineEvent>

    fun publish(event: PipelineEvent)
}

/**
 * Production-ready implementation of PipelineEventBus.
 */
@Singleton
class DefaultPipelineEventBus @Inject constructor() : PipelineEventBus {

    private val _events = MutableSharedFlow<PipelineEvent>(extraBufferCapacity = 100)
    override val events: SharedFlow<PipelineEvent> = _events.asSharedFlow()

    override fun publish(event: PipelineEvent) {
        _events.tryEmit(event)
        IntentLogger.d("PipelineEventBus", "Published Event: ${event.javaClass.simpleName} (ID: ${event.eventId})")
    }
}
