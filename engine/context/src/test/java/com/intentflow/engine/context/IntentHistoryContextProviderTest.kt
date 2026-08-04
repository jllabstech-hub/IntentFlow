package com.intentflow.engine.context

import com.intentflow.engine.context.provider.IntentHistoryContextProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IntentHistoryContextProviderTest {

    private val provider = IntentHistoryContextProvider()

    @Test
    fun testRecordIntentExecutionAndFrequency() {
        provider.recordIntentExecution("messaging.send", mapOf("recipient" to "Mom"))
        provider.recordIntentExecution("messaging.send", mapOf("recipient" to "Mom"))
        provider.recordIntentExecution("phone.call", mapOf("contact" to "Mom"))

        val recent = provider.getRecentIntents(10)
        assertEquals("phone.call", recent.first())
        assertEquals("messaging.send", recent[1])

        val frequent = provider.getFrequentlyUsedValues(10)
        assertTrue(frequent.containsKey("Mom"))
    }
}
