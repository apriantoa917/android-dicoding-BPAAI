package com.aprianto.dicostory.ui.dashboard.explore

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aprianto.dicostory.R
import com.aprianto.dicostory.data.model.Story
import com.aprianto.dicostory.data.viewmodel.StoryViewModel
import com.aprianto.dicostory.databinding.CustomTooltipMapsExploreBinding
import com.aprianto.dicostory.databinding.FragmentExploreBinding
import com.aprianto.dicostory.ui.dashboard.MainActivity
import com.aprianto.dicostory.ui.detail.DetailActivity
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
import java.lang.StringBuilder

class ExploreFragment : Fragment(), OnMapReadyCallback, GoogleMap.InfoWindowAdapter,
    AdapterView.OnItemSelectedListener {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentExploreBinding
    private val storyViewModel: StoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExploreBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        /* multilingual text for spinner */
        val zoomLevel = arrayOf(
            getString(R.string.const_text_adapter_maps_default),
            getString(R.string.const_text_adapter_maps_province),
            getString(R.string.const_text_adapter_maps_city),
            getString(R.string.const_text_adapter_maps_district),
            getString(R.string.const_text_adapter_maps_around)
        )

        /* set up dropdown location scope */
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item, zoomLevel
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.zoomType.adapter = adapter
        binding.zoomType.onItemSelectedListener = this

        val mapFragment =
            (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
        mapFragment.getMapAsync(this)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isTiltGesturesEnabled = true

        /* init story with location data -> add markers */
        storyViewModel.storyList.observe(viewLifecycleOwner) { storyList ->
            for (story in storyList) {
                mMap.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            story.lat?.toDouble() ?: 0.0,
                            story.lon?.toDouble() ?: 0.0
                        )
                    )
                )?.tag = story
            }
        }

        mMap.setInfoWindowAdapter(this)
        mMap.setOnInfoWindowClickListener { marker ->
            val data: Story = marker.tag as Story
            routeToDetailStory(data)
        }
        getMyLocation()
        setMapStyle()

        storyViewModel.loadStoryLocationData(
            requireContext(),
            (activity as MainActivity).getUserToken()
        )
        storyViewModel.coordinateTemp.observe(this) {
            CameraUpdateFactory.newLatLngZoom(it, 4f)
        }
    }

    private fun routeToDetailStory(data: Story) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra(Constanta.StoryDetail.UserName.name, data.name)
        intent.putExtra(Constanta.StoryDetail.ImageURL.name, data.photoUrl)
        intent.putExtra(
            Constanta.StoryDetail.ContentDescription.name,
            data.description
        )
        intent.putExtra(
            Constanta.StoryDetail.UploadTime.name,
            /*
            dynamic set uploaded time locally
                en : uploaded + on + 30 April 2022 00.00
                id : diupload + pada + 30 April 2022 00.00
            */
            "${requireContext().getString(R.string.const_text_uploaded)} ${
                requireContext().getString(
                    R.string.const_text_time_on
                )
            } ${Helper.getUploadStoryTime(data.createdAt)}"
        )
        intent.putExtra(Constanta.StoryDetail.Latitude.name, data.lat.toString())
        intent.putExtra(Constanta.StoryDetail.Longitude.name, data.lon.toString())
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        requireContext().startActivity(intent)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            } else {
                Helper.notifyGivePermission(
                    requireContext(),
                    getString(R.string.UI_validation_permission_location)
                )
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                (activity as MainActivity),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    storyViewModel.coordinateTemp.postValue(
                        LatLng(
                            location.latitude,
                            location.longitude
                        )
                    )
                } else {
                    storyViewModel.coordinateTemp.postValue(Constanta.indonesiaLocation)
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        (activity as MainActivity),
                        R.raw.gmaps_style
                    )
                )
            if (!success) {
                Log.e(Constanta.TAG_MAPS, "Style parsing  failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(Constanta.TAG_MAPS, "Can't find style. Error: ", exception)
        }
    }

    override fun getInfoWindow(marker: Marker): View {
        val bindingTooltips =
            CustomTooltipMapsExploreBinding.inflate(LayoutInflater.from(requireContext()))
        val data: Story = marker.tag as Story
        bindingTooltips.labelLocation.text = Helper.parseAddressLocation(
            requireContext(),
            marker.position.latitude, marker.position.longitude
        )
        bindingTooltips.name.text = StringBuilder("Story by ").append(data.name)
        bindingTooltips.image.setImageBitmap(Helper.bitmapFromURL(requireContext(), data.photoUrl))
        bindingTooltips.storyDescription.text = data.description
        bindingTooltips.storyUploadTime.text = Helper.getUploadStoryTime(data.createdAt)
        return bindingTooltips.root
    }

    override fun getInfoContents(marker: Marker): View? {
        return null
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val level: Float = when (position) {
            0 -> 4f
            1 -> 8f
            2 -> 11f
            3 -> 14f
            4 -> 17f
            else -> 4f
        }
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(storyViewModel.coordinateTemp.value!!, level)
        )
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(Constanta.indonesiaLocation, 4f)
        )
    }


}

