package com.intentflow.presentation.renderer

import androidx.compose.runtime.Composable
import com.intentflow.core.model.UISchema
import com.intentflow.dynamicui.RenderUiSchemaScreen
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Surface Target Classification.
 */
enum class SurfaceTarget {
    MOBILE_COMPOSE,
    WEAR_OS,
    ANDROID_AUTO,
    ANDROID_TV,
    DESKTOP
}

/**
 * Abstract Surface UI Renderer Interface.
 */
interface IntentUiRenderer {
    val targetSurface: SurfaceTarget

    @Composable
    fun RenderUi(
        uiSchema: UISchema,
        onValueChange: (slotName: String, value: String) -> Unit,
        onActionSubmit: (uiSchema: UISchema) -> Unit
    )
}

/**
 * Production Mobile Phone Jetpack Compose Material 3 Surface Renderer.
 */
@Singleton
class ComposeUiRenderer @Inject constructor() : IntentUiRenderer {

    override val targetSurface: SurfaceTarget = SurfaceTarget.MOBILE_COMPOSE

    @Composable
    override fun RenderUi(
        uiSchema: UISchema,
        onValueChange: (slotName: String, value: String) -> Unit,
        onActionSubmit: (uiSchema: UISchema) -> Unit
    ) {
        RenderUiSchemaScreen(
            uiSchema = uiSchema,
            onValueChange = onValueChange,
            onActionSubmit = onActionSubmit
        )
    }
}
