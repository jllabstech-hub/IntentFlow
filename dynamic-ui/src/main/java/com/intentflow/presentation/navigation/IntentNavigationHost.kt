package com.intentflow.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.intentflow.core.model.UISchema
import com.intentflow.presentation.renderer.IntentUiRenderer

/**
 * Multi-Screen Intent Navigation Host.
 */
@Composable
fun IntentNavigationHost(
    uiRenderer: IntentUiRenderer,
    currentUiSchema: UISchema?,
    onValueChange: (slotName: String, value: String) -> Unit,
    onActionSubmit: (uiSchema: UISchema) -> Unit,
    modifier: Modifier = Modifier
) {
    if (currentUiSchema != null) {
        uiRenderer.RenderUi(
            uiSchema = currentUiSchema,
            onValueChange = onValueChange,
            onActionSubmit = onActionSubmit
        )
    }
}
