package com.intentflow.engine.session

import com.intentflow.core.model.PipelineStage
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class IntentSessionManagerTest {

    private lateinit var sessionManager: IntentSessionManager

    @Before
    fun setup() {
        sessionManager = DefaultIntentSessionManager()
    }

    @Test
    fun testCreateAndResumeSession() = runBlocking {
        val session = sessionManager.createSession("Set alarm for 7 AM")

        assertNotNull(session)
        assertEquals("Set alarm for 7 AM", session.userInputHistory.first())
        assertEquals(PipelineStage.INPUT_RECEIVED, session.currentPipelineStage)

        val resumed = sessionManager.resumeSession(session.sessionId)
        assertNotNull(resumed)
        assertEquals(session.sessionId, resumed?.sessionId)
    }

    @Test
    fun testArchiveAndDeleteSession() = runBlocking {
        val session = sessionManager.createSession("Call Mom")
        sessionManager.archiveSession(session.sessionId)

        assertNull(sessionManager.activeSession.value)

        sessionManager.deleteSession(session.sessionId)
        val deleted = sessionManager.resumeSession(session.sessionId)
        assertNull(deleted)
    }
}
