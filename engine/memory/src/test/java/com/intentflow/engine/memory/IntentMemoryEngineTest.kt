package com.intentflow.engine.memory

import com.intentflow.core.model.IntentMemoryEntry
import com.intentflow.core.model.MemoryCategory
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class IntentMemoryEngineTest {

    private lateinit var memoryEngine: IntentMemoryEngine

    @Before
    fun setup() {
        memoryEngine = DefaultIntentMemoryEngine()
    }

    @Test
    fun testSaveAndRetrieveSlotMemory() = runBlocking {
        val entry = IntentMemoryEntry(
            memoryId = "mem_1",
            domain = "travel",
            slotName = "home_airport",
            preferredValue = "SFO",
            displayLabel = "Home Airport: San Francisco",
            category = MemoryCategory.SLOT_PREFERENCE
        )

        memoryEngine.saveMemory(entry)

        val retrieved = memoryEngine.getMemoryForSlot("travel", "home_airport")
        assertNotNull(retrieved)
        assertEquals("SFO", retrieved?.preferredValue)
    }

    @Test
    fun testUpdateAndDeleteMemory() = runBlocking {
        val entry = IntentMemoryEntry(
            memoryId = "mem_2",
            domain = "payments",
            slotName = "default_upi",
            preferredValue = "Google Pay",
            displayLabel = "Preferred Payment",
            category = MemoryCategory.PAYMENT_PREFERENCE
        )

        memoryEngine.saveMemory(entry)
        memoryEngine.updateMemoryValue("mem_2", "PhonePe")

        val updated = memoryEngine.getMemoryForSlot("payments", "default_upi")
        assertEquals("PhonePe", updated?.preferredValue)

        memoryEngine.deleteMemory("mem_2")
        val deleted = memoryEngine.getMemoryForSlot("payments", "default_upi")
        assertNull(deleted)
    }
}
