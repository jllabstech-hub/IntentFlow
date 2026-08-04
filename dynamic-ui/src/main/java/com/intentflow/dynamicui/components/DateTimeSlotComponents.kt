package com.intentflow.dynamicui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.intentflow.core.model.SlotMetadata

@Composable
fun DatePickerSlotComponent(
    metadata: SlotMetadata,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = metadata.currentRawValue ?: "",
        onValueChange = onValueChange,
        label = { Text(metadata.displayName) },
        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Date") },
        placeholder = { Text("YYYY-MM-DD or 'Tomorrow'") },
        singleLine = true,
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)
    )
}

@Composable
fun TimePickerSlotComponent(
    metadata: SlotMetadata,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = metadata.currentRawValue ?: "",
        onValueChange = onValueChange,
        label = { Text(metadata.displayName) },
        leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = "Time") },
        placeholder = { Text("HH:MM or '7 AM'") },
        singleLine = true,
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)
    )
}
