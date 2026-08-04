package com.intentflow.engine.context.provider

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * On-device Provider for Installed Applications list using PackageManager.
 */
@Singleton
class AppContextProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getInstalledApps(limit: Int = 20): List<String> {
        val appNames = mutableListOf<String>()
        try {
            val pm = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolvedApps = pm.queryIntentActivities(mainIntent, 0)

            for (resolveInfo in resolvedApps) {
                if (appNames.size >= limit) break
                val label = resolveInfo.loadLabel(pm).toString()
                if (label.isNotBlank() && !appNames.contains(label)) {
                    appNames.add(label)
                }
            }
        } catch (e: Exception) {
            // Ignore package manager errors
        }
        return appNames
    }
}
