package com.intentflow.engine.session

import com.intentflow.core.model.IntentSession
import com.intentflow.core.model.PipelineStage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Intent Session Manager Interface.
 * Manages creation, persistence, restoration, archiving, and deletion of user interaction sessions.
 */
interface IntentSessionManager {
    val activeSession: StateFlow<IntentSession?>

    suspend fun createSession(rawInput: String): IntentSession
    suspend fun saveSession(session: IntentSession)
    suspend fun resumeSession(sessionId: String): IntentSession?
    suspend fun restoreActiveSession(): IntentSession?
    suspend fun archiveSession(sessionId: String)
    suspend fun deleteSession(sessionId: String)
    fun observeSessions(): Flow<List<IntentSession>>
}

/**
 * Production-ready implementation of IntentSessionManager.
 */
@Singleton
class DefaultIntentSessionManager @Inject constructor() : IntentSessionManager {

    private val sessionRegistry = ConcurrentHashMap<String, IntentSession>()
    private val archivedRegistry = ConcurrentHashMap<String, IntentSession>()

    private val _activeSession = MutableStateFlow<IntentSession?>(null)
    override val activeSession: StateFlow<IntentSession?> = _activeSession.asStateFlow()

    override suspend fun createSession(rawInput: String): IntentSession {
        val session = IntentSession(
            sessionId = UUID.randomUUID().toString(),
            userInputHistory = listOf(rawInput),
            currentPipelineStage = PipelineStage.INPUT_RECEIVED
        )
        sessionRegistry[session.sessionId] = session
        _activeSession.value = session
        return session
    }

    override suspend fun saveSession(session: IntentSession) {
        sessionRegistry[session.sessionId] = session
        if (_activeSession.value?.sessionId == session.sessionId) {
            _activeSession.value = session
        }
    }

    override suspend fun resumeSession(sessionId: String): IntentSession? {
        val session = sessionRegistry[sessionId] ?: archivedRegistry[sessionId]
        if (session != null) {
            _activeSession.value = session
        }
        return session
    }

    override suspend fun restoreActiveSession(): IntentSession? {
        return activeSession.value
    }

    override suspend fun archiveSession(sessionId: String) {
        val session = sessionRegistry.remove(sessionId)
        if (session != null) {
            archivedRegistry[sessionId] = session.copy(currentPipelineStage = PipelineStage.SUSPENDED)
        }
        if (_activeSession.value?.sessionId == sessionId) {
            _activeSession.value = null
        }
    }

    override suspend fun deleteSession(sessionId: String) {
        sessionRegistry.remove(sessionId)
        archivedRegistry.remove(sessionId)
        if (_activeSession.value?.sessionId == sessionId) {
            _activeSession.value = null
        }
    }

    override fun observeSessions(): Flow<List<IntentSession>> {
        return flowOf(sessionRegistry.values.toList())
    }
}
