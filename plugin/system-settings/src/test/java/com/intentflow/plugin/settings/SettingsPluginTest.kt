package com.intentflow.plugin.settings

import android.content.Context
import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.SlotValue
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SettingsPluginTest {

    private val context: Context = mockk(relaxed = true)
    private lateinit var settingsPlugin: SettingsPlugin

    @Before
    fun setup() {
        settingsPlugin = SettingsPlugin(context)
    }

    @Test
    fun testToggleWifiExecution() = runBlocking {
        val intentObject = IntentObject(
            id = "1",
            intentId = "settings.toggle_wifi",
            domain = "settings",
            slots = mapOf("state" to SlotValue("true", "true"))
        )

        val result = settingsPlugin.execute(intentObject)
        assertTrue(result is ExecutionResult.Success)
        assertEquals("settings.toggle_wifi", (result as ExecutionResult.Success).intentId)
    }
}
