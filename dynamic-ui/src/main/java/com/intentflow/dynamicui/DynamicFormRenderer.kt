package com.intentflow.dynamicui

import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.SlotDefinition

/**
 * Base interface for the Dynamic UI Form Renderer.
 * Responsible for mapping IntentObject slot definitions to memoized Jetpack Compose components.
 */
interface DynamicFormRenderer {
    fun getComponentForSlot(slotDefinition: SlotDefinition)
}
