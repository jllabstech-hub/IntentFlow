package com.intentflow.engine.intent.runtime

import com.intentflow.core.model.ExecutionTarget
import com.intentflow.core.model.IntentObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExecutionPlannerTest {

    private val planner = ExecutionPlanner()

    @Test
    fun testBuildExecutionPlanPluginTarget() {
        val intentObject = IntentObject(
            id = "1",
            intentId = "messaging.send",
            domain = "messaging",
            metadata = mapOf("executionMapping" to "plugin.telephony:send_sms")
        )

        val plan = planner.buildExecutionPlan(intentObject)

        assertNotNull(plan)
        assertEquals("messaging.send", plan.intentId)
        assertEquals(ExecutionTarget.ANDROID_PLUGIN, plan.target)
        assertEquals("plugin.telephony:send_sms", plan.targetHandlerId)
    }

    @Test
    fun testBuildExecutionPlanBankingRequiresConfirmation() {
        val intentObject = IntentObject(
            id = "1",
            intentId = "banking.transfer",
            domain = "banking"
        )

        val plan = planner.buildExecutionPlan(intentObject)
        assertEquals(true, plan.requiresUserConfirmation)
        assertNotNull(plan.confirmationMessage)
    }
}
