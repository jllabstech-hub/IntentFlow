package com.intentflow.tooling.platform

import com.intentflow.core.model.CatalogGenerationOptions
import com.intentflow.core.model.ToolingCommand
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class IntentFlowToolsTest {

    private lateinit var tools: IntentFlowTools

    @Before
    fun setup() {
        tools = DefaultIntentFlowTools()
    }

    @Test
    fun testGenerateAndPackageCatalogZip() = runBlocking {
        val options = CatalogGenerationOptions(targetVersionCode = 2, targetVersionName = "2.0.0")
        val outFile = File("catalog-v2.json")
        val genResult = tools.generateCatalog(emptyList(), options, outFile)

        assertNotNull(genResult)
        assertEquals(ToolingCommand.GENERATE_CATALOG, genResult.command)
        assertTrue(genResult.isSuccess)

        val zipFile = File("catalog-v2.zip")
        val packResult = tools.packageZip(outFile, zipFile)

        assertNotNull(packResult)
        assertEquals(ToolingCommand.PACKAGE_CATALOG_ZIP, packResult.command)
        assertTrue(packResult.isSuccess)
    }

    @Test
    fun testValidateCatalogCommand() = runBlocking {
        val valResult = tools.validateCatalog(File("catalog-v1.json"))

        assertNotNull(valResult)
        assertEquals(ToolingCommand.VALIDATE_CATALOG, valResult.command)
        assertTrue(valResult.isSuccess)
    }
}
