package com.intentflow.catalog.validation

import com.intentflow.core.model.CatalogData
import com.intentflow.core.model.CatalogVersion
import com.intentflow.core.model.DomainDefinition
import com.intentflow.core.model.IntentDefinition
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CatalogValidationPipelineTest {

    private lateinit var pipeline: CatalogValidationPipeline

    @Before
    fun setup() {
        pipeline = DefaultCatalogValidationPipeline(
            DuplicateIdEvaluator(),
            BrokenReferenceEvaluator(),
            MissingSlotEvaluator(),
            UnusedEntityEvaluator(),
            GraphCycleEvaluator(),
            RegexSyntaxEvaluator(),
            PermissionConflictEvaluator(),
            SchemaCompatibilityEvaluator(),
            VersionCompatibilityEvaluator()
        )
    }

    @Test
    fun testValidCatalogPassesValidationPipeline() = runBlocking {
        val catalog = CatalogData(
            version = CatalogVersion(1, "1.0.0"),
            domains = listOf(DomainDefinition("messaging", "Messaging", "Desc")),
            intents = listOf(IntentDefinition("messaging.send", "Send", "Desc", "messaging"))
        )

        val report = pipeline.validateCatalog(catalog)
        assertTrue(report.isValid)
        assertEquals(0, report.criticalErrorCount)
    }

    @Test
    fun testDuplicateIdDetectionFailsValidation() = runBlocking {
        val catalog = CatalogData(
            version = CatalogVersion(1, "1.0.0"),
            domains = listOf(DomainDefinition("messaging", "Messaging", "Desc")),
            intents = listOf(
                IntentDefinition("messaging.send", "Send 1", "Desc", "messaging"),
                IntentDefinition("messaging.send", "Send 2", "Desc", "messaging")
            )
        )

        val report = pipeline.validateCatalog(catalog)
        assertFalse(report.isValid)
        assertEquals(1, report.criticalErrorCount)
    }
}
