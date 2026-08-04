package com.intentflow.engine.intent.runtime

import com.intentflow.core.model.IntentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thread-safe State Machine managing Intent Runtime lifecycle transitions.
 */
@Singleton
class RuntimeStateMachine @Inject constructor() {

    private val _state = MutableStateFlow<IntentState>(IntentState.Idle)
    val state: StateFlow<IntentState> = _state.asStateFlow()

    fun transitionTo(newState: IntentState) {
        _state.value = newState
    }

    fun reset() {
        _state.value = IntentState.Idle
    }
}
