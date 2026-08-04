package com.intentflow.catalog.runtime

import com.intentflow.core.model.CatalogData
import com.intentflow.core.model.CatalogVersion
import com.intentflow.core.model.DomainDefinition
import com.intentflow.core.model.EntityDefinition
import com.intentflow.core.model.IntentDefinition
import com.intentflow.core.model.ValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogValidatorTest {

    private val validator = DefaultCatalogValidator()

    @Test
    fun testValidCatalogPassesValidation() {
        val validCatalog = CatalogData(
            version = CatalogVersion(1, "1.0.0"),
            domains = listOf(
                DomainDefinition(
                    id = "messaging",
                    displayName = "Messaging",
                    description = "Messaging domain",
                    intents = listOf(
                        IntentDefinition("messaging.send", "Send Message", "Sends a message", "messaging")
                    )
                )
            ),
            entities = listOf(
                EntityDefinition("contact", "Contact Entity")
            )
        )

        val result = validator.validate(validCatalog)
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun testDuplicateDomainIdFailsValidation() {
        val invalidCatalog = CatalogData(
            version = CatalogVersion(1, "1.0.0"),
            domains = listOf(
                DomainDefinition("messaging", "Messaging 1", "Desc 1"),
                DomainDefinition("messaging", "Messaging 2", "Desc 2")
            )
        )

        val result = validator.validate(invalidCatalog)
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Duplicate domain ID found: 'messaging'", (result as ValidationResult.Invalid).reason)
    }

    @Test
    fun testDuplicateIntentIdFailsValidation() {
        val invalidCatalog = CatalogData(
            version = CatalogVersion(1, "1.0.0"),
            domains = listOf(
                DomainDefinition(
                    id = "d1",
                    displayName = "Domain 1",
                    description = "Desc",
                    intents = listOf(
                        IntentDefinition("same.intent", "Name 1", "Desc", "d1")
                    )
                ),
                DomainDefinition(
                    id = "d2",
                    displayName = "Domain 2",
                    description = "Desc",
                    intents = listOf(
                        IntentDefinition("same.intent", "Name 2", "Desc", "d2")
                    )
                )
            )
        )

        val result = validator.validate(invalidCatalog)
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Duplicate intent ID found: 'same.intent'", (result as ValidationResult.Invalid).reason)
    }
}
