package com.intentflow.engine.intent.telemetry

import com.intentflow.core.common.logger.IntentLogger
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Metric event for local telemetry tracking.
 */
data class TelemetryEvent(
    val eventName: String,
    val intentId: String?,
    val durationMs: Long,
    val success: Boolean,
    val details: Map<String, String> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 100% On-Device Telemetry Manager.
 * Logs runtime pipeline performance metrics and step durations locally with zero cloud upload.
 */
@Singleton
class LocalTelemetryManager @Inject constructor() {

    private val eventLog = CopyOnWriteArrayList<TelemetryEvent>()

    fun logPipelineStep(stepName: String, intentId: String?, durationMs: Long, success: Boolean, details: Map<String, String> = emptyMap()) {
        val event = TelemetryEvent(stepName, intentId, durationMs, success, details)
        eventLog.add(event)
        if (eventLog.size > 500) {
            eventLog.removeAt(0)
        }
        IntentLogger.d("Telemetry", "Step '$stepName' finished in ${durationMs}ms for intent '$intentId' (Success: $success)")
    }

    fun getLocalEvents(): List<TelemetryEvent> = eventLog.toList()

    fun clear() {
        eventLog.clear()
    }
}
