package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Supported UI component type descriptors.
 */
@Serializable
enum class UiComponentType {
    TEXT_FIELD,
    STEPPER,
    DROPDOWN,
    DATE_PICKER,
    TIME_PICKER,
    SEARCH_FIELD,
    SWITCH_TOGGLE,
    MEDIA_PICKER,
    DIALOG_MODAL,
    BOTTOM_SHEET_MODAL
}

/**
 * Layout arrangement and styling specification.
 */
@Serializable
data class LayoutRules(
    val orientation: String = "VERTICAL",
    val spacingDp: Int = 8,
    val paddingDp: Int = 16,
    val alignment: String = "START"
)

/**
 * Visibility rules evaluating conditional show/hide behavior.
 */
@Serializable
data class VisibilityRules(
    val isVisible: Boolean = true,
    val dependsOnSlot: String? = null,
    val expectedValue: String? = null
)

/**
 * Live validation display rules.
 */
@Serializable
data class ValidationUiRules(
    val showInlineError: Boolean = true,
    val errorMessage: String? = null,
    val regexPattern: String? = null
)

/**
 * User interaction and focus rules.
 */
@Serializable
data class InteractionRules(
    val isEnabled: Boolean = true,
    val isReadOnly: Boolean = false,
    val autoFocus: Boolean = false
)

/**
 * Animation and transition rules.
 */
@Serializable
data class AnimationRules(
    val enterTransition: String = "FADE_IN",
    val exitTransition: String = "FADE_OUT",
    val durationMs: Int = 300
)

/**
 * Accessibility rules for Screen Readers / TalkBack.
 */
@Serializable
data class AccessibilityRules(
    val contentDescription: String,
    val semanticRole: String = "BUTTON",
    val heading: Boolean = false
)

/**
 * Individual field component UI specification.
 */
@Serializable
data class UiComponentSpec(
    val componentId: String,
    val slotName: String,
    val componentType: UiComponentType,
    val label: String,
    val currentValue: String? = null,
    val options: List<String> = emptyList(),
    val layout: LayoutRules = LayoutRules(),
    val visibility: VisibilityRules = VisibilityRules(),
    val validation: ValidationUiRules = ValidationUiRules(),
    val interaction: InteractionRules = InteractionRules(),
    val animation: AnimationRules = AnimationRules(),
    val accessibility: AccessibilityRules
)

/**
 * Root UI Schema specification for a complete screen.
 */
@Serializable
data class UISchema(
    val schemaId: String,
    val intentId: String,
    val title: String,
    val subtitle: String? = null,
    val components: List<UiComponentSpec>,
    val layout: LayoutRules = LayoutRules(),
    val animation: AnimationRules = AnimationRules(),
    val actionButtonText: String = "Submit"
)
