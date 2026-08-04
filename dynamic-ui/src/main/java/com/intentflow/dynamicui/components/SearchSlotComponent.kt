package com.intentflow.dynamicui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.intentflow.core.model.SlotMetadata

@Composable
fun SearchSlotComponent(
    metadata: SlotMetadata,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = metadata.currentRawValue ?: "",
        onValueChange = onValueChange,
        label = { Text(metadata.displayName) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        singleLine = true,
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)
    )
}
