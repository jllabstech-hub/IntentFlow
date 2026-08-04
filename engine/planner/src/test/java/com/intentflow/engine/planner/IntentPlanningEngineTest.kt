package com.intentflow.engine.planner

import com.intentflow.core.model.ExecutionTarget
import com.intentflow.core.model.IntentObject
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class IntentPlanningEngineTest {

    private lateinit var planningEngine: IntentPlanningEngine

    @Before
    fun setup() {
        planningEngine = DefaultIntentPlanningEngine()
    }

    @Test
    fun testCreatePlanResolvesPluginTarget() = runBlocking {
        val intentObject = IntentObject(
            id = "1",
            intentId = "plugin.telephony:send_sms",
            domain = "messaging"
        )

        val plan = planningEngine.createPlan(intentObject)

        assertNotNull(plan)
        assertEquals(ExecutionTarget.ANDROID_PLUGIN, plan.target)
    }

    @Test
    fun testBankingDomainRequiresConfirmation() = runBlocking {
        val intentObject = IntentObject(
            id = "1",
            intentId = "banking.transfer",
            domain = "banking"
        )

        val plan = planningEngine.createPlan(intentObject)
        assertEquals(true, plan.requiresUserConfirmation)
    }
}
