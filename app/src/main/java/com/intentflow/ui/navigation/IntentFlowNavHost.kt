package com.intentflow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.intentflow.ui.screen.HomeScreen
import com.intentflow.ui.screen.IntentSessionScreen
import com.intentflow.ui.screen.SettingsScreen

/**
 * IntentFlow Navigation Host.
 *
 * Wires all [IntentFlowDestination] routes to their corresponding Compose screens.
 * Every screen delegates to the `:dynamic-ui` module for rendering — no
 * domain-specific UI lives inside this graph.
 *
 * @param navController The [NavHostController] managing the back stack.
 * @param modifier Modifier applied to the [NavHost] root layout.
 */
@Composable
fun IntentFlowNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = IntentFlowDestination.Home.route,
        modifier = modifier
    ) {
        composable(route = IntentFlowDestination.Home.route) {
            HomeScreen(
                onNavigateToSession = { sessionId ->
                    navController.navigate(
                        IntentFlowDestination.IntentSession.createRoute(sessionId)
                    )
                }
            )
        }

        composable(
            route = IntentFlowDestination.IntentSession.route,
            arguments = listOf(
                navArgument(IntentFlowDestination.IntentSession.ARG_SESSION_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val sessionId = requireNotNull(
                backStackEntry.arguments?.getString(
                    IntentFlowDestination.IntentSession.ARG_SESSION_ID
                )
            ) { "sessionId argument is required for IntentSession destination" }

            IntentSessionScreen(
                sessionId = sessionId,
                onNavigateToResult = { id ->
                    navController.navigate(
                        IntentFlowDestination.ExecutionResult.createRoute(id)
                    ) {
                        popUpTo(IntentFlowDestination.Home.route)
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = IntentFlowDestination.ExecutionResult.route,
            arguments = listOf(
                navArgument(IntentFlowDestination.ExecutionResult.ARG_SESSION_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val sessionId = requireNotNull(
                backStackEntry.arguments?.getString(
                    IntentFlowDestination.ExecutionResult.ARG_SESSION_ID
                )
            ) { "sessionId argument is required for ExecutionResult destination" }

            com.intentflow.ui.screen.ExecutionResultScreen(
                sessionId = sessionId,
                onNavigateHome = {
                    navController.navigate(IntentFlowDestination.Home.route) {
                        popUpTo(IntentFlowDestination.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = IntentFlowDestination.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
