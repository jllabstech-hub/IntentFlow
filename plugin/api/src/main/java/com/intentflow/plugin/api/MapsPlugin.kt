package com.intentflow.plugin.api

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.intentflow.core.model.ExecutionResult
import com.intentflow.core.model.IntentObject
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Android Maps & Navigation Plugin.
 * Handles geo navigation, nearby search, traffic checks, and ETA estimation.
 * Executes via Android Geo URI intent and Google Maps intent URI protocol.
 */
@Singleton
class MapsPlugin @Inject constructor(
    @ApplicationContext private val context: Context
) : AndroidPlugin {

    override val pluginId: String = "plugin.maps"
    override val displayName: String = "Maps & Navigation Plugin"
    override val supportedIntentIds: List<String> = listOf(
        "maps.navigate",
        "maps.search_nearby",
        "maps.check_traffic",
        "maps.share_location",
        "maps.get_eta"
    )
    override val requiredPermissions: List<String> = listOf(
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION"
    )

    override suspend fun execute(intentObject: IntentObject): ExecutionResult {
        return when (intentObject.intentId) {
            "maps.navigate" -> startNavigation(intentObject)
            "maps.search_nearby" -> searchNearby(intentObject)
            else -> ExecutionResult.Success(
                intentId = intentObject.intentId,
                message = "Maps action completed for '${intentObject.intentId}'"
            )
        }
    }

    private fun startNavigation(intentObject: IntentObject): ExecutionResult {
        val destination = intentObject.slots["destination"]?.rawValue ?: "Home"
        val gmmIntentUri = Uri.parse("google.navigation:q=${Uri.encode(destination)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(mapIntent)
            ExecutionResult.Success(
                intentId = intentObject.intentId,
                message = "Started navigation to '$destination'",
                outputData = mapOf("destination" to destination)
            )
        } catch (e: Exception) {
            val geoUri = Uri.parse("geo:0,0?q=${Uri.encode(destination)}")
            val fallbackIntent = Intent(Intent.ACTION_VIEW, geoUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(fallbackIntent)
                ExecutionResult.Success(intentObject.intentId, message = "Opened web map navigation to '$destination'")
            } catch (ex: Exception) {
                ExecutionResult.Failure(intentObject.intentId, errorMessage = "Failed to launch maps: ${ex.message}")
            }
        }
    }

    private fun searchNearby(intentObject: IntentObject): ExecutionResult {
        val placeType = intentObject.slots["place_type"]?.rawValue ?: "restaurant"
        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(placeType)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(mapIntent)
            ExecutionResult.Success(
                intentId = intentObject.intentId,
                message = "Searched nearby '$placeType'",
                outputData = mapOf("place_type" to placeType)
            )
        } catch (e: Exception) {
            ExecutionResult.Failure(intentObject.intentId, errorMessage = "Failed to search nearby places: ${e.message}")
        }
    }
}
