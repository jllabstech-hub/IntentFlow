package com.intentflow.dynamicui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.intentflow.core.model.SlotMetadata

@Composable
fun SwitchSlotComponent(
    metadata: SlotMetadata,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isChecked = metadata.currentRawValue?.lowercase() in listOf("true", "yes", "1", "on")

    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = metadata.displayName,
            style = MaterialTheme.typography.bodyMedium
        )
        Switch(
            checked = isChecked,
            onCheckedChange = { onValueChange(it.toString()) }
        )
    }
}
