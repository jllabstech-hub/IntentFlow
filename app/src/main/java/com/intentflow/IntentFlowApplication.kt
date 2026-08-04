package com.intentflow

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * IntentFlow Application entry point.
 *
 * Responsibilities:
 * - Initialises the Hilt dependency injection graph.
 * - Plants the Timber logging tree for debug builds.
 *
 * This class contains no business logic.
 * Business logic lives in `:engine:*` and `:catalog:*` modules.
 */
@HiltAndroidApp
class IntentFlowApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initLogging()
    }

    /**
     * Plants a [Timber.DebugTree] in debug builds only.
     * Release builds emit no logs from Timber to protect user privacy.
     */
    private fun initLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.i("IntentFlow started — architecture v1.0")
    }
}
