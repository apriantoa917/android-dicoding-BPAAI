package com.aprianto.dicostory.ui.dashboard.story

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.aprianto.dicostory.R
import com.aprianto.dicostory.data.viewmodel.StoryViewModel
import com.aprianto.dicostory.databinding.ActivityNewStoryPickLocationBinding
import com.aprianto.dicostory.databinding.CustomTooltipPickLocationStoryBinding
import com.aprianto.dicostory.utils.Constanta
import com.aprianto.dicostory.utils.Helper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class NewStoryPickLocation : AppCompatActivity(), OnMapReadyCallback, GoogleMap.InfoWindowAdapter {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityNewStoryPickLocationBinding
    val viewModel: StoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewStoryPickLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnCancel.setOnClickListener {
            viewModel.isLocationPicked.postValue(false)
            finish()
        }
        binding.btnSelectLocation.setOnClickListener {
            /* check is location picked before next step */
            if (viewModel.isLocationPicked.value == true) {
                val intent = Intent()
                intent.putExtra(
                    Constanta.LocationPicker.IsPicked.name,
                    viewModel.isLocationPicked.value
                )
                intent.putExtra(
                    Constanta.LocationPicker.Latitude.name,
                    viewModel.coordinateLatitude.value
                )
                intent.putExtra(
                    Constanta.LocationPicker.Longitude.name,
                    viewModel.coordinateLongitude.value
                )
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Helper.showDialogInfo(
                    this,
                    getString(R.string.UI_validation_maps_select_area)
                )
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isTiltGesturesEnabled = true
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constanta.indonesiaLocation, 4f))
        mMap.setInfoWindowAdapter(this)
        mMap.setOnInfoWindowClickListener { marker ->
            postLocationSelected(marker.position.latitude, marker.position.longitude)
            marker.hideInfoWindow()
        }
        mMap.setOnMapClickListener {
            mMap.clear()
            mMap.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            it.latitude,
                            it.longitude
                        )
                    )
            )?.showInfoWindow()
        }
        mMap.setOnPoiClickListener {
            Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
            mMap.clear()
            mMap.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            it.latLng.latitude,
                            it.latLng.longitude
                        )
                    )
            )?.showInfoWindow()
        }
        setMapStyle()
        getMyLastLocation()
    }

    private fun postLocationSelected(lat: Double, lon: Double) {
        val address =
            Helper.parseAddressLocation(
                this,
                lat,
                lon
            )
        binding.addressBar.text = address
        viewModel.isLocationPicked.postValue(true)
        viewModel.coordinateLatitude.postValue(lat)
        viewModel.coordinateLongitude.postValue(lon)
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        this,
                        R.raw.gmaps_style
                    )
                )
            if (!success) {
                Log.e("MAPS", "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e("MAPS", "Can't find style. Error: ", exception)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }
                else -> {
                    // No location access granted.
                }
            }
        }

    /* check permission in this activity -> related to fusedLocation requirements*/
    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                /* if user location fetched -> add marker & trigger input to user location */
                location?.let {
                    mMap.addMarker(
                        MarkerOptions().position(LatLng(it.latitude, it.longitude))
                    )
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 20f)
                    )
                    postLocationSelected(it.latitude, it.longitude)
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun getInfoContents(marker: Marker): View? {
        return null
    }

    /* while marker touched -> show custom view */
    override fun getInfoWindow(marker: Marker): View {
        val bindingTooltips =
            CustomTooltipPickLocationStoryBinding.inflate(LayoutInflater.from(this))
        bindingTooltips.location.text = Helper.parseAddressLocation(
            this,
            marker.position.latitude, marker.position.longitude
        )
        return bindingTooltips.root
    }
}