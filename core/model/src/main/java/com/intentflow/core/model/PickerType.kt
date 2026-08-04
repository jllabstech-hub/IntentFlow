package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Defines the UI picker type mapped to a slot component.
 */
@Serializable
enum class PickerType {
    TEXT_INPUT,
    CHIP_SELECTION,
    DROPDOWN,
    DATE_PICKER,
    TIME_PICKER,
    STEPPER,
    SWITCH,
    BOTTOM_SHEET,
    DIALOG,
    SEARCH_FIELD,
    IMAGE_PICKER,
    FILE_PICKER,
    LOCATION_PICKER,
    CONTACT_PICKER,
    COLOR_PICKER,
    RADIO_GROUP,
    CHECKBOX_GROUP
}
