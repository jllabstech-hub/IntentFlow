package com.intentflow.engine.intent

import com.intentflow.core.model.ContextObject
import com.intentflow.core.model.SlotDefinition
import com.intentflow.core.model.SlotType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SlotSuggestionEngineTest {

    private val suggestionEngine = SlotSuggestionEngine()

    @Test
    fun testGenerateSuggestionsStaticList() {
        val slot = SlotDefinition("type", "Type", SlotType.ENUM, suggestions = listOf("Option 1", "Option 2"))
        val suggestions = suggestionEngine.generateSuggestions(slot)
        assertEquals(2, suggestions.size)
        assertEquals("Option 1", suggestions.first())
    }

    @Test
    fun testGenerateSuggestionsDateTypeFallback() {
        val slot = SlotDefinition("date", "Date", SlotType.DATE)
        val suggestions = suggestionEngine.generateSuggestions(slot)
        assertTrue(suggestions.contains("Today"))
        assertTrue(suggestions.contains("Tomorrow"))
    }

    @Test
    fun testGenerateSuggestionsContactTypeContextIntegration() {
        val slot = SlotDefinition("contact", "Contact", SlotType.CONTACT)
        val context = ContextObject(recentContacts = listOf("Alice", "Bob"))

        val suggestions = suggestionEngine.generateSuggestions(slot, context)
        assertEquals(2, suggestions.size)
        assertEquals("Alice", suggestions.first())
    }
}
