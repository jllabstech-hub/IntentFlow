package com.intentflow.engine.benchmark

import com.intentflow.core.model.BenchmarkScenario
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BenchmarkRunnerTest {

    private lateinit var runner: BenchmarkRunner

    @Before
    fun setup() {
        runner = DefaultBenchmarkRunner()
    }

    @Test
    fun testRunBenchmarkSuiteCalculatesMetrics() = runBlocking {
        val scenario = BenchmarkScenario(
            scenarioId = "scen_1",
            name = "Test Alarm Intent Suite",
            description = "Evaluates alarm intent accuracy",
            testUtterances = listOf("Set alarm for 7 AM", "Wake me up at 6 AM")
        )

        val reports = runner.runSuite(listOf(scenario))

        assertNotNull(reports)
        assertEquals(1, reports.size)

        val report = reports.first()
        assertEquals(98.5f, report.intentAccuracyPercent)
        assertEquals(8L, report.latency.p50Ms)
        assertTrue(report.charts.isNotEmpty())
    }
}
