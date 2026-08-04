package com.intentflow.dynamicui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.intentflow.core.model.SlotMetadata

@Composable
fun StepperSlotComponent(
    metadata: SlotMetadata,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentValue = (metadata.currentRawValue ?: metadata.defaultValue ?: "0").toIntOrNull() ?: 0

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = metadata.displayName,
            style = MaterialTheme.typography.bodyMedium
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onValueChange((currentValue - 1).coerceAtLeast(0).toString()) }) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease")
            }
            Text(
                text = currentValue.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            IconButton(onClick = { onValueChange((currentValue + 1).toString()) }) {
                Icon(Icons.Default.Add, contentDescription = "Increase")
            }
        }
    }
}
