package com.intentflow.core.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExecutionPlanTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testExecutionPlanSerialization() {
        val plan = ExecutionPlan(
            intentId = "messaging.send",
            target = ExecutionTarget.ANDROID_PLUGIN,
            targetHandlerId = "plugin.telephony",
            resolvedParameters = mapOf("recipient" to "+1234567890", "message" to "Hello!")
        )

        val serialized = json.encodeToString(plan)
        val deserialized = json.decodeFromString<ExecutionPlan>(serialized)

        assertEquals(plan, deserialized)
    }

    @Test
    fun testValidationPassesForValidPlan() {
        val plan = ExecutionPlan(
            intentId = "alarm.set",
            target = ExecutionTarget.ANDROID_PLUGIN,
            targetHandlerId = "plugin.alarm"
        )
        assertTrue(plan.validate() is ValidationResult.Valid)
    }

    @Test
    fun testValidationFailsWhenConfirmationRequiredWithoutMessage() {
        val invalidPlan = ExecutionPlan(
            intentId = "payment.send",
            target = ExecutionTarget.ANDROID_PLUGIN,
            targetHandlerId = "plugin.payment",
            requiresUserConfirmation = true,
            confirmationMessage = null
        )
        val result = invalidPlan.validate()
        assertTrue(result is ValidationResult.Invalid)
    }
}
