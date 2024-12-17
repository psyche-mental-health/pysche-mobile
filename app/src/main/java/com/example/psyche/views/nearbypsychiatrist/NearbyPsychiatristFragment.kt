package com.example.psyche.views.nearbypsychiatrist

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.psyche.BuildConfig
import com.example.psyche.databinding.FragmentNearbyPsychiatristBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient

class NearbyPsychiatristFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: NearbyPsychiatristViewModel by viewModels()
    private var _binding: FragmentNearbyPsychiatristBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNearbyPsychiatristBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
        }

        val textView = TextView(context).apply {
            text = "Nearby Psychiatrists"
            textSize = 20f
            setTextColor(resources.getColor(android.R.color.white, null))
        }
        binding.toolbar.addView(textView)
        val layoutParams = textView.layoutParams as androidx.appcompat.widget.Toolbar.LayoutParams
        layoutParams.gravity = Gravity.CENTER
        textView.layoutParams = layoutParams


        val mapFragment =
            childFragmentManager.findFragmentById(binding.map.id) as? SupportMapFragment
        if (mapFragment == null) {
            val newMapFragment = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction().replace(binding.map.id, newMapFragment).commit()
            newMapFragment.getMapAsync(this)
        } else {
            mapFragment.getMapAsync(this)
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        Places.initialize(requireContext(), BuildConfig.GOOGLE_MAPS_API_KEY)
        placesClient = Places.createClient(requireContext())

        checkLocationPermission()

        viewModel.initializePlacesClient(BuildConfig.GOOGLE_MAPS_API_KEY)
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            viewModel.setLocationPermissionGranted(true)
        }
    }

    private fun getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                    viewModel.fetchNearbyPsychiatrists(currentLatLng)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.setLocationPermissionGranted(true)
                getDeviceLocation()
            } else {
                viewModel.setLocationPermissionGranted(false)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        viewModel.locationPermissionGranted.observe(viewLifecycleOwner) { granted ->
            if (granted) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    map.isMyLocationEnabled = true
                    getDeviceLocation()
                }
            }
        }

        viewModel.nearbyPsychiatrists.observe(viewLifecycleOwner) { psychiatrists ->
            map.clear()
            for (psychiatrist in psychiatrists) {
                placesClient.fetchPlace(
                    FetchPlaceRequest.builder(
                        psychiatrist.placeId,
                        listOf(Place.Field.LAT_LNG)
                    ).build()
                )
                    .addOnSuccessListener { response: FetchPlaceResponse ->
                        response.place.latLng?.let { latLng: LatLng ->
                            map.addMarker(MarkerOptions().position(latLng).title(psychiatrist.name))
                        }
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}