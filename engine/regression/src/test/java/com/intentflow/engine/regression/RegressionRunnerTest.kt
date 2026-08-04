package com.intentflow.engine.regression

import com.intentflow.core.model.RegressionSuite
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RegressionRunnerTest {

    private lateinit var runner: RegressionRunner

    @Before
    fun setup() {
        runner = DefaultRegressionRunner()
    }

    @Test
    fun testEvaluateCatalogUpdateNoRegressionsFound() = runBlocking {
        val report = runner.evaluateCatalogUpdate(
            baselineVersionCode = 1,
            targetVersionCode = 2,
            testUtterances = listOf("Send SMS to Mom", "Call Alice")
        )

        assertNotNull(report)
        assertTrue(report.isPassed)
        assertEquals(0, report.totalRegressionsFound)
        assertEquals(2, report.totalUtterancesTested)
    }

    @Test
    fun testRunRegressionSuiteFlowProgress() = runBlocking {
        val suite = RegressionSuite("suite_1", "Test Suite", 1, 2, listOf("Call Mom"))
        val progress = runner.runRegressionSuite(suite).first()

        assertEquals(20, progress)
    }
}
