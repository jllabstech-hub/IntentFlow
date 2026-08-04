package com.intentflow.presentation.console

import com.intentflow.core.model.ConsoleTab
import com.intentflow.tooling.platform.IntentFlowTools
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class PlatformConsoleControllerTest {

    private val toolingPlatform: IntentFlowTools = mockk(relaxed = true)
    private lateinit var controller: PlatformConsoleController

    @Before
    fun setup() {
        controller = DefaultPlatformConsoleController(toolingPlatform)
    }

    @Test
    fun testSelectTabUpdatesConsoleState() {
        controller.selectTab(ConsoleTab.PROVIDER_COMPARISON)

        val state = controller.consoleState.value
        assertEquals(ConsoleTab.PROVIDER_COMPARISON, state.activeTab)
        assertEquals("Switched to PROVIDER_COMPARISON Panel", state.statusMessage)
    }

    @Test
    fun testCompareProvidersExecutesTask() = runBlocking {
        controller.compareProviders(listOf("gemma", "gemini"))

        val state = controller.consoleState.value
        assertNotNull(state)
        assertEquals("Provider comparison complete. Winner: Gemma (On-Device)", state.statusMessage)
    }
}
