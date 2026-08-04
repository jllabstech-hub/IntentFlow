package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Enumeration of all supported Slot Types in IntentFlow.
 */
@Serializable
enum class SlotType {
    CONTACT,
    LOCATION,
    DATE,
    TIME,
    CURRENCY,
    AMOUNT,
    IMAGE,
    VIDEO,
    FILE,
    NUMBER,
    TEXT,
    BOOLEAN,
    ENUM,
    MULTI_SELECT,
    EMAIL,
    PHONE,
    URL
}
