package com.intentflow.dynamicui.modals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.intentflow.core.model.SlotMetadata
import com.intentflow.dynamicui.RenderSlotComponent

@Composable
fun SlotDialog(
    metadata: SlotMetadata,
    onValueSubmit: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var currentValue = metadata.currentRawValue ?: ""

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Fill Required Slot: ${metadata.displayName}") },
        text = {
            RenderSlotComponent(
                metadata = metadata,
                onValueChange = { currentValue = it }
            )
        },
        confirmButton = {
            Button(onClick = { onValueSubmit(currentValue) }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlotBottomSheet(
    metadata: SlotMetadata,
    onValueSubmit: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var currentValue = metadata.currentRawValue ?: ""

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp)
        ) {
            Text(
                text = "Select ${metadata.displayName}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            RenderSlotComponent(
                metadata = metadata,
                onValueChange = { currentValue = it }
            )
            Button(
                onClick = { onValueSubmit(currentValue) },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Done")
            }
        }
    }
}
