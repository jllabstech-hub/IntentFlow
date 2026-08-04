package com.intentflow.plugin.api

import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PluginCapabilityRegistryTest {

    private val mockPlugin: AndroidPlugin = mockk()

    @Test
    fun testPluginRegistryResolvesAndExecutes() = runBlocking {
        coEvery { mockPlugin.pluginId } returns "plugin.mock"
        coEvery { mockPlugin.supportedIntentIds } returns listOf("test.intent")
        coEvery { mockPlugin.execute(any()) } returns ExecutionResult.Success("test.intent", "Executed")

        val registry = PluginCapabilityRegistry(setOf(mockPlugin))

        val resolved = registry.getPluginForIntent("test.intent")
        assertNotNull(resolved)
        assertEquals("plugin.mock", resolved?.pluginId)

        val result = registry.executeIntent(IntentObject("1", "test.intent", "test"))
        assertTrue(result is ExecutionResult.Success)
    }
}
