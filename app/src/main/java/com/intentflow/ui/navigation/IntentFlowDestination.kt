package com.intentflow.ui.navigation

/**
 * Sealed hierarchy defining all top-level navigation destinations in IntentFlow.
 *
 * Architecture rule: Screens are never feature-specific (e.g. no FlightScreen).
 * The router dispatches to generic intent renderers based on [IntentObject] state.
 *
 * @property route Unique navigation route string.
 */
sealed class IntentFlowDestination(val route: String) {

    /** Main intent input and suggestion surface. */
    data object Home : IntentFlowDestination(route = "home")

    /** Dynamic intent form renderer — route includes the active intent session ID. */
    data object IntentSession : IntentFlowDestination(route = "intent_session/{sessionId}") {
        /** Creates a fully-qualified navigation route with [sessionId] embedded. */
        fun createRoute(sessionId: String): String = "intent_session/$sessionId"

        /** Argument key for extracting the session ID from the back stack entry. */
        const val ARG_SESSION_ID = "sessionId"
    }

    /** Intent execution result surface. */
    data object ExecutionResult : IntentFlowDestination(route = "execution_result/{sessionId}") {
        fun createRoute(sessionId: String): String = "execution_result/$sessionId"
        const val ARG_SESSION_ID = "sessionId"
    }

    /** Application settings and provider selection. */
    data object Settings : IntentFlowDestination(route = "settings")
}
