package com.intentflow.engine.planner

import com.intentflow.core.model.ExecutionTargetCapability
import com.intentflow.core.model.IntentObject
import com.intentflow.plugin.api.CapabilityRegistry
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class CapabilityExecutionPlannerTest {

    private val capabilityRegistry: CapabilityRegistry = mockk(relaxed = true)
    private lateinit var planner: CapabilityExecutionPlanner

    @Before
    fun setup() {
        planner = DefaultCapabilityExecutionPlanner(capabilityRegistry)
    }

    @Test
    fun testResolveCapabilitySelectsAndroidPlugin() = runBlocking {
        val intentObject = IntentObject(
            id = "1",
            intentId = "messaging.send",
            domain = "messaging",
            metadata = mapOf("executionMapping" to "plugin.telephony:send_sms")
        )

        val plan = planner.resolveCapability(intentObject)

        assertNotNull(plan)
        assertEquals(ExecutionTargetCapability.ANDROID_PLUGIN, plan.selectedCapability)
        assertEquals("plugin.telephony:send_sms", plan.handlerId)
    }

    @Test
    fun testResolveCapabilitySelectsDeepLink() = runBlocking {
        val intentObject = IntentObject(
            id = "1",
            intentId = "maps.navigate",
            domain = "maps",
            metadata = mapOf("deepLink" to "google.navigation:q=Paris")
        )

        val plan = planner.resolveCapability(intentObject)

        assertNotNull(plan)
        assertEquals(ExecutionTargetCapability.SYSTEM_DEEP_LINK, plan.selectedCapability)
        assertEquals("google.navigation:q=Paris", plan.handlerId)
    }
}
