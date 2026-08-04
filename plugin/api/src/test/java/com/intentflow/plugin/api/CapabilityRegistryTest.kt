package com.intentflow.plugin.api

import com.intentflow.core.model.CapabilityDescriptor
import com.intentflow.core.model.CapabilityProviderType
import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CapabilityRegistryTest {

    private val mockExecutor: CapabilityExecutor = mockk()
    private val desc = CapabilityDescriptor(
        capabilityId = "plugin.telephony",
        providerType = CapabilityProviderType.ANDROID_PLUGIN,
        supportedIntentIds = listOf("messaging.send"),
        displayName = "Telephony SMS Plugin",
        priority = 100
    )

    private lateinit var registry: CapabilityRegistry

    @Before
    fun setup() {
        coEvery { mockExecutor.descriptor } returns desc
        registry = DefaultCapabilityRegistry(setOf(mockExecutor))
    }

    @Test
    fun testFindExecutorsForIntent() {
        val executors = registry.findExecutorsForIntent("messaging.send")

        assertNotNull(executors)
        assertEquals(1, executors.size)
        assertEquals("plugin.telephony", executors.first().descriptor.capabilityId)
    }

    @Test
    fun testUnregisterCapability() {
        registry.unregisterCapability("plugin.telephony")
        val executors = registry.findExecutorsForIntent("messaging.send")
        assertTrue(executors.isEmpty())
    }
}
