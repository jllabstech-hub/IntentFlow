package com.intentflow.engine.replay

import com.intentflow.core.model.IntentObject
import com.intentflow.core.model.IntentSession
import com.intentflow.core.model.ReplayEnvironmentConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ReplayTestSuiteRunnerTest {

    private lateinit var runner: ReplayTestSuiteRunner

    private val sampleSession = IntentSession(
        sessionId = "sess_test_100",
        userInputHistory = listOf("Send SMS to Mom"),
        currentIntentObject = IntentObject("1", "messaging.send", "messaging")
    )

    @Before
    fun setup() {
        runner = DefaultReplayTestSuiteRunner()
    }

    @Test
    fun testReplaySessionSingleFlow() = runBlocking {
        val config = ReplayEnvironmentConfig(targetProviderId = "gemma-2b-it")
        val diff = runner.replaySession(sampleSession, config).first()

        assertNotNull(diff)
        assertEquals("sess_test_100", diff.sessionId)
        assertTrue(diff.isReplayedSuccess)
    }

    @Test
    fun testCompareCatalogsBatch() = runBlocking {
        val report = runner.compareCatalogs(listOf(sampleSession), catalogVersionA = 1, catalogVersionB = 2)

        assertNotNull(report)
        assertEquals(1, report.totalSessionsReplayed)
        assertEquals(100.0f, report.overallMatchPercent)
    }
}
