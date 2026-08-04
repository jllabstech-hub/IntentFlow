package com.intentflow.dynamicui

import com.intentflow.core.model.AccessibilityRules
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.IntentSchema
import com.intentflow.core.model.PickerType
import com.intentflow.core.model.SlotType
import com.intentflow.core.model.UISchema
import com.intentflow.core.model.UiComponentSpec
import com.intentflow.core.model.UiComponentType
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Factory interface translating IntentObject & IntentSchema into a pure UISchema.
 */
interface UiSchemaFactory {
    fun createUiSchema(
        intentObject: IntentObject,
        schema: IntentSchema
    ): UISchema
}

/**
 * Production-ready implementation of UiSchemaFactory.
 */
@Singleton
class DefaultUiSchemaFactory @Inject constructor() : UiSchemaFactory {

    override fun createUiSchema(intentObject: IntentObject, schema: IntentSchema): UISchema {
        val components = schema.slots.map { slotDef ->
            val currentValue = intentObject.slots[slotDef.slotName]?.rawValue ?: slotDef.defaultValue

            val componentType = when {
                slotDef.pickerType == PickerType.STEPPER || slotDef.slotType == SlotType.NUMBER -> UiComponentType.STEPPER
                slotDef.pickerType == PickerType.SWITCH || slotDef.slotType == SlotType.BOOLEAN -> UiComponentType.SWITCH_TOGGLE
                slotDef.pickerType == PickerType.DROPDOWN || slotDef.slotType == SlotType.ENUM -> UiComponentType.DROPDOWN
                slotDef.pickerType == PickerType.SEARCH_FIELD -> UiComponentType.SEARCH_FIELD
                slotDef.pickerType == PickerType.DATE_PICKER || slotDef.slotType == SlotType.DATE -> UiComponentType.DATE_PICKER
                slotDef.pickerType == PickerType.TIME_PICKER || slotDef.slotType == SlotType.TIME -> UiComponentType.TIME_PICKER
                slotDef.pickerType in listOf(PickerType.CONTACT_PICKER, PickerType.LOCATION_PICKER, PickerType.IMAGE_PICKER, PickerType.FILE_PICKER) -> UiComponentType.MEDIA_PICKER
                else -> UiComponentType.TEXT_FIELD
            }

            UiComponentSpec(
                componentId = "comp_${slotDef.slotName}",
                slotName = slotDef.slotName,
                componentType = componentType,
                label = slotDef.displayName,
                currentValue = currentValue,
                options = slotDef.suggestions,
                accessibility = AccessibilityRules(
                    contentDescription = "Fill ${slotDef.displayName}",
                    semanticRole = "TEXT_INPUT"
                )
            )
        }

        return UISchema(
            schemaId = UUID.randomUUID().toString(),
            intentId = intentObject.intentId,
            title = intentObject.intentId,
            subtitle = "Domain: ${intentObject.domain}",
            components = components,
            actionButtonText = if (intentObject.isComplete) "Execute Intent" else "Fill Required Slots"
        )
    }
}
