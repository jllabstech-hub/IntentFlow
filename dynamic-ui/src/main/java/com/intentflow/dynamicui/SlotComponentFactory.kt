package com.intentflow.dynamicui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.intentflow.core.model.PickerType
import com.intentflow.core.model.SlotMetadata
import com.intentflow.core.model.SlotType
import com.intentflow.dynamicui.components.DatePickerSlotComponent
import com.intentflow.dynamicui.components.DropdownSlotComponent
import com.intentflow.dynamicui.components.PickerSlotComponent
import com.intentflow.dynamicui.components.SearchSlotComponent
import com.intentflow.dynamicui.components.StepperSlotComponent
import com.intentflow.dynamicui.components.SwitchSlotComponent
import com.intentflow.dynamicui.components.TextFieldSlotComponent
import com.intentflow.dynamicui.components.TimePickerSlotComponent

@Composable
fun RenderSlotComponent(
    metadata: SlotMetadata,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        metadata.pickerType == PickerType.STEPPER -> {
            StepperSlotComponent(metadata = metadata, onValueChange = onValueChange, modifier = modifier)
        }
        metadata.pickerType == PickerType.SWITCH -> {
            SwitchSlotComponent(metadata = metadata, onValueChange = onValueChange, modifier = modifier)
        }
        metadata.pickerType == PickerType.DROPDOWN || metadata.slotType == SlotType.ENUM -> {
            DropdownSlotComponent(metadata = metadata, onValueChange = onValueChange, modifier = modifier)
        }
        metadata.pickerType == PickerType.SEARCH_FIELD -> {
            SearchSlotComponent(metadata = metadata, onValueChange = onValueChange, modifier = modifier)
        }
        metadata.pickerType == PickerType.DATE_PICKER || metadata.slotType == SlotType.DATE -> {
            DatePickerSlotComponent(metadata = metadata, onValueChange = onValueChange, modifier = modifier)
        }
        metadata.pickerType == PickerType.TIME_PICKER || metadata.slotType == SlotType.TIME -> {
            TimePickerSlotComponent(metadata = metadata, onValueChange = onValueChange, modifier = modifier)
        }
        metadata.pickerType in listOf(PickerType.CONTACT_PICKER, PickerType.LOCATION_PICKER, PickerType.IMAGE_PICKER, PickerType.FILE_PICKER) -> {
            PickerSlotComponent(metadata = metadata, onValueChange = onValueChange, modifier = modifier)
        }
        else -> {
            TextFieldSlotComponent(metadata = metadata, onValueChange = onValueChange, modifier = modifier)
        }
    }
}
