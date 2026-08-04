package com.intentflow.dynamicui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.IntentSchema
import com.intentflow.core.model.SlotMetadata

/**
 * 100% Behavioral Intent Schema-Driven Dynamic Compose Renderer Screen.
 * UI renders exclusively from IntentSchema (behavior, pickers, layout rules), separating UI rendering from static IntentDefinition metadata.
 */
@Composable
fun DynamicIntentScreen(
    intentObject: IntentObject,
    schema: IntentSchema,
    slotMetadatas: List<SlotMetadata>,
    onSlotValueChange: (String, String) -> Unit,
    onExecuteIntent: (IntentObject) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            // Header: Rendered from IntentSchema behavior and intent state
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = schema.intentId,
                            style = MaterialTheme.typography.titleLarge
                        )
                        AssistChip(
                            onClick = {},
                            label = { Text("Confidence: ${(intentObject.confidence * 100).toInt()}%") }
                        )
                    }
                    Text(
                        text = "Layout Mode: ${schema.dynamicUiRules["layout"] ?: "vertical"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = if (intentObject.isComplete) "Parameters Ready" else "Interactive Slot Filling",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Dynamic Form Body Driven by IntentSchema Slot Metadata
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(slotMetadatas) { metadata ->
                    RenderSlotComponent(
                        metadata = metadata,
                        onValueChange = { newValue -> onSlotValueChange(metadata.slotName, newValue) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Action Button
            Button(
                onClick = { onExecuteIntent(intentObject) },
                enabled = intentObject.isComplete,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(if (intentObject.isComplete) "Execute Intent" else "Fill Required Slots")
            }
        }
    }
}
