package com.intentflow.engine.intent.runtime

import com.intentflow.core.model.IntentState
import org.junit.Assert.assertEquals
import org.junit.Test

class RuntimeStateMachineTest {

    private val stateMachine = RuntimeStateMachine()

    @Test
    fun testInitialStateIsIdle() {
        assertEquals(IntentState.Idle, stateMachine.state.value)
    }

    @Test
    fun testStateTransitionAndReset() {
        stateMachine.transitionTo(IntentState.ProcessingInput("test"))
        assertEquals(IntentState.ProcessingInput("test"), stateMachine.state.value)

        stateMachine.reset()
        assertEquals(IntentState.Idle, stateMachine.state.value)
    }
}
