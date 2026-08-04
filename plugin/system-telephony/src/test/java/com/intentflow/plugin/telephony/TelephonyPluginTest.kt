package com.intentflow.plugin.telephony

import android.content.Context
import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.SlotValue
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TelephonyPluginTest {

    private val context: Context = mockk(relaxed = true)
    private lateinit var telephonyPlugin: TelephonyPlugin

    @Before
    fun setup() {
        telephonyPlugin = TelephonyPlugin(context)
    }

    @Test
    fun testSupportedIntents() {
        assertTrue(telephonyPlugin.supportedIntentIds.contains("messaging.send"))
        assertTrue(telephonyPlugin.supportedIntentIds.contains("phone.call"))
    }

    @Test
    fun testSendSmsExecution() = runBlocking {
        val intentObject = IntentObject(
            id = "1",
            intentId = "messaging.send",
            domain = "messaging",
            slots = mapOf(
                "recipient" to SlotValue("Mom", "Mom"),
                "message_text" to SlotValue("Hello", "Hello")
            )
        )

        val result = telephonyPlugin.execute(intentObject)
        assertTrue(result is ExecutionResult.Success)
        assertEquals("messaging.send", (result as ExecutionResult.Success).intentId)
    }
}
