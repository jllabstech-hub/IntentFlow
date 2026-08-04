package com.intentflow.dynamicui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.intentflow.core.model.SlotMetadata
import com.intentflow.core.model.ValidationResult

@Composable
fun TextFieldSlotComponent(
    metadata: SlotMetadata,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isError = metadata.validationResult is ValidationResult.Invalid

    Column(modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = metadata.currentRawValue ?: "",
            onValueChange = onValueChange,
            label = { Text(metadata.displayName) },
            isError = isError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (isError) {
            val reason = (metadata.validationResult as ValidationResult.Invalid).reason
            Text(
                text = reason,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 2.dp)
            )
        }
    }
}
