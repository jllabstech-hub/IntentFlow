package com.intentflow.dynamicui.developer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.intentflow.core.model.PipelineTraceInspector

/**
 * Developer Mode Inspector View rendering the 8-stage pipeline trace:
 * Raw Input → Detected Intent → Extracted Slots → Context → Execution JSON → Provider → Latency → Output
 */
@Composable
fun DeveloperModeTraceViewer(
    trace: PipelineTraceInspector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Developer Mode Pipeline Trace",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Step 1: Raw Input
            TraceStepCard(stepNumber = 1, title = "Raw Input", content = trace.rawInput)

            // Step 2: Detected Intent
            TraceStepCard(
                stepNumber = 2,
                title = "Detected Intent",
                content = "${trace.detectedIntentId ?: "None"} (Confidence: ${(trace.confidence * 100).toInt()}%)"
            )

            // Step 3: Extracted Slots
            TraceStepCard(
                stepNumber = 3,
                title = "Extracted Slots",
                content = if (trace.extractedSlots.isNotEmpty()) {
                    trace.extractedSlots.entries.joinToString("\n") { "${it.key}: ${it.value}" }
                } else "No slots extracted"
            )

            // Step 4: Context
            TraceStepCard(stepNumber = 4, title = "Context", content = trace.contextSummary)

            // Step 5: Execution JSON
            TraceStepCard(stepNumber = 5, title = "Execution JSON Payload", content = trace.executionJson)

            // Step 6: Provider
            TraceStepCard(stepNumber = 6, title = "Active Provider", content = trace.providerId)

            // Step 7: Latency
            TraceStepCard(stepNumber = 7, title = "Execution Latency", content = "${trace.latencyMs} ms")

            // Step 8: Output
            TraceStepCard(
                stepNumber = 8,
                title = "Final Output Result",
                content = trace.outputMessage,
                isHighlight = true
            )
        }
    }
}

@Composable
private fun TraceStepCard(
    stepNumber: Int,
    title: String,
    content: String,
    isHighlight: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlight) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Step $stepNumber: $title",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
