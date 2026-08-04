package com.intentflow.engine.context.provider

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class LocationContextInfo(
    val latitude: Double?,
    val longitude: Double?,
    val locationName: String?
)

/**
 * On-device Provider for Location signals (GPS coordinates and reverse geocoded city name).
 */
@Singleton
class LocationContextProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getCurrentLocation(): LocationContextInfo {
        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val lastKnown: Location? = locationManager?.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                ?: locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (lastKnown != null) {
                val cityName = reverseGeocode(lastKnown.latitude, lastKnown.longitude)
                return LocationContextInfo(
                    latitude = lastKnown.latitude,
                    longitude = lastKnown.longitude,
                    locationName = cityName
                )
            }
        } catch (e: SecurityException) {
            // Permission not granted
        } catch (e: Exception) {
            // Location service error
        }
        return LocationContextInfo(latitude = null, longitude = null, locationName = null)
    }

    private fun reverseGeocode(lat: Double, lng: Double): String? {
        try {
            if (Geocoder.isPresent()) {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    return address.locality ?: address.subAdminArea ?: address.adminArea ?: address.countryName
                }
            }
        } catch (e: Exception) {
            // Reverse geocoding failed or offline
        }
        return null
    }
}
