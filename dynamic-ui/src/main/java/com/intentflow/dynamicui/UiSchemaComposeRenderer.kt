package com.intentflow.dynamicui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.intentflow.core.model.PickerType
import com.intentflow.core.model.SlotMetadata
import com.intentflow.core.model.SlotType
import com.intentflow.core.model.UISchema
import com.intentflow.core.model.UiComponentType

/**
 * Deterministic Jetpack Compose Material 3 Renderer that renders screens exclusively from a UISchema.
 */
@Composable
fun RenderUiSchemaScreen(
    uiSchema: UISchema,
    onValueChange: (String, String) -> Unit,
    onActionSubmit: (UISchema) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(uiSchema.layout.paddingDp.dp)
        ) {
            // Header: Rendered from UISchema title & subtitle
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = uiSchema.title,
                        style = MaterialTheme.typography.titleLarge
                    )
                    uiSchema.subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Body: Render Components Specified in UISchema
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(uiSchema.components) { spec ->
                    if (spec.visibility.isVisible) {
                        val slotType = when (spec.componentType) {
                            UiComponentType.STEPPER -> SlotType.NUMBER
                            UiComponentType.SWITCH_TOGGLE -> SlotType.BOOLEAN
                            UiComponentType.DROPDOWN -> SlotType.ENUM
                            UiComponentType.DATE_PICKER -> SlotType.DATE
                            UiComponentType.TIME_PICKER -> SlotType.TIME
                            else -> SlotType.TEXT
                        }
                        val pickerType = when (spec.componentType) {
                            UiComponentType.STEPPER -> PickerType.STEPPER
                            UiComponentType.SWITCH_TOGGLE -> PickerType.SWITCH
                            UiComponentType.DROPDOWN -> PickerType.DROPDOWN
                            UiComponentType.DATE_PICKER -> PickerType.DATE_PICKER
                            UiComponentType.TIME_PICKER -> PickerType.TIME_PICKER
                            UiComponentType.SEARCH_FIELD -> PickerType.SEARCH_FIELD
                            UiComponentType.MEDIA_PICKER -> PickerType.FILE_PICKER
                            else -> PickerType.TEXT_INPUT
                        }

                        val slotMetadata = SlotMetadata(
                            slotName = spec.slotName,
                            displayName = spec.label,
                            slotType = slotType,
                            pickerType = pickerType,
                            currentRawValue = spec.currentValue,
                            suggestions = spec.options
                        )

                        RenderSlotComponent(
                            metadata = slotMetadata,
                            onValueChange = { newValue -> onValueChange(spec.slotName, newValue) },
                            modifier = Modifier.semantics {
                                contentDescription = spec.accessibility.contentDescription
                            }
                        )
                        Spacer(modifier = Modifier.height(spec.layout.spacingDp.dp))
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Footer Submit Action Button
            Button(
                onClick = { onActionSubmit(uiSchema) },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(uiSchema.actionButtonText)
            }
        }
    }
}
