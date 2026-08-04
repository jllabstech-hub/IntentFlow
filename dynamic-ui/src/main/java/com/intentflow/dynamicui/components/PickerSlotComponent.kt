package com.intentflow.dynamicui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.intentflow.core.model.PickerType
import com.intentflow.core.model.SlotMetadata

@Composable
fun PickerSlotComponent(
    metadata: SlotMetadata,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = when (metadata.pickerType) {
        PickerType.CONTACT_PICKER -> Icons.Default.Person
        PickerType.LOCATION_PICKER -> Icons.Default.Place
        PickerType.IMAGE_PICKER -> Icons.Default.Image
        PickerType.FILE_PICKER -> Icons.Default.AttachFile
        else -> Icons.Default.Place
    }

    OutlinedTextField(
        value = metadata.currentRawValue ?: "",
        onValueChange = onValueChange,
        label = { Text(metadata.displayName) },
        leadingIcon = { Icon(icon, contentDescription = metadata.displayName) },
        singleLine = true,
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)
    )
}
