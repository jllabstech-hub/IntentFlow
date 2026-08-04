package com.intentflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.intentflow.ui.navigation.IntentFlowNavHost
import com.intentflow.ui.theme.IntentFlowTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * IntentFlow single-activity host.
 *
 * This activity is the only activity in the application.
 * All navigation is handled by [IntentFlowNavHost] inside Jetpack Compose.
 * No business logic lives here — the activity is a pure Compose host.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Timber.d("MainActivity created")

        setContent {
            IntentFlowApp()
        }
    }
}

/**
 * Root composable for the IntentFlow application.
 *
 * Applies the [IntentFlowTheme] and sets up the [IntentFlowNavHost].
 * The [NavHostController] is retained across recompositions via [rememberNavController].
 */
@Composable
private fun IntentFlowApp() {
    IntentFlowTheme {
        val navController = rememberNavController()
        Surface(modifier = Modifier.fillMaxSize()) {
            IntentFlowNavHost(navController = navController)
        }
    }
}
