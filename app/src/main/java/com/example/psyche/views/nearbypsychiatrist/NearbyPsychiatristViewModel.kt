package com.example.psyche.views.nearbypsychiatrist

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient

class NearbyPsychiatristViewModel(application: Application) : AndroidViewModel(application) {
    private val _locationPermissionGranted = MutableLiveData<Boolean>()
    val locationPermissionGranted: LiveData<Boolean> get() = _locationPermissionGranted

    private val _nearbyPsychiatrists = MutableLiveData<List<Psychiatrist>>()
    val nearbyPsychiatrists: LiveData<List<Psychiatrist>> get() = _nearbyPsychiatrists

    private lateinit var placesClient: PlacesClient

    fun setLocationPermissionGranted(granted: Boolean) {
        _locationPermissionGranted.value = granted
    }

    fun initializePlacesClient(apiKey: String) {
        Places.initialize(getApplication(), apiKey)
        placesClient = Places.createClient(getApplication())
    }

    fun fetchNearbyPsychiatrists(currentLatLng: LatLng) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery("psikiater")
            .setLocationBias(
                RectangularBounds.newInstance(
                    LatLng(currentLatLng.latitude - 0.1, currentLatLng.longitude - 0.1),
                    LatLng(currentLatLng.latitude + 0.1, currentLatLng.longitude + 0.1)
                ))
            .build()

        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
                val psychiatrists = response.autocompletePredictions.mapNotNull { prediction: AutocompletePrediction ->
                    prediction.placeId?.let { placeId ->
                        Psychiatrist(prediction.getPrimaryText(null).toString(), placeId)
                    }
                }
                _nearbyPsychiatrists.value = psychiatrists
            }.addOnFailureListener { exception ->
                Log.e("NearbyPsychiatristVM", "Place not found: ${exception.message}")
            }
        } else {
            Log.e("NearbyPsychiatristVM", "Location permission not granted")
        }
    }
}

data class Psychiatrist(val name: String, val placeId: String)