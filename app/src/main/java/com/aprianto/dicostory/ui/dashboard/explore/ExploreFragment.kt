package com.aprianto.dicostory.ui.dashboard.explore

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aprianto.dicostory.R
import com.aprianto.dicostory.data.model.Story
import com.aprianto.dicostory.data.viewmodel.StoryViewModel
import com.aprianto.dicostory.data.viewmodel.ViewModelGeneralFactory
import com.aprianto.dicostory.databinding.CustomTooltipMapsExploreBinding
import com.aprianto.dicostory.databinding.FragmentExploreBinding
import com.aprianto.dicostory.ui.dashboard.MainActivity
import com.aprianto.dicostory.ui.detail.DetailActivity
import com.aprianto.dicostory.utils.Constanta
import com.aprianto.dicostory.utils.Helper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.lang.StringBuilder


class ExploreFragment : Fragment(), OnMapReadyCallback, GoogleMap.InfoWindowAdapter {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentExploreBinding
    val storyViewModel: StoryViewModel by viewModels {
        ViewModelGeneralFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExploreBinding.inflate(inflater, container, false)

        /* allow marker show from url */
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val mapFragment =
            (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
        mapFragment.getMapAsync(this)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true


        val indonesia = LatLng(-2.3932797, 108.8507139)
        storyViewModel.storyList.observe(viewLifecycleOwner) { storyList ->
            for (story in storyList) {
                mMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(story.lat as Double, story.lon as Double))
                        .title("Story by ${story.name}")
                        .snippet(story.description)
//                        .icon(Helper.getStoryMapPreview(requireContext(), story.photoUrl)
                        .icon(
                            vectorToBitmap(
                                R.drawable.ic_baseline_mms_24,
                                (activity as MainActivity).applicationContext.getColor(R.color.red)
                            )

                        )
                )?.tag = story

            }
        }

        storyViewModel.loadStoryLocationData()
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(indonesia, 4f))

        mMap.setInfoWindowAdapter(this)

        mMap.setOnInfoWindowClickListener { marker ->
            val data: Story = marker.tag as Story
            routeToDetailStory(data)
        }

        mMap.setOnInfoWindowCloseListener {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(indonesia, 4f))
        }

        getMyLocation()
        setMapStyle()
    }


    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
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
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                (activity as MainActivity),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
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
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }

    override fun getInfoWindow(marker: Marker): View {
        val bindingTooltips =
            CustomTooltipMapsExploreBinding.inflate(LayoutInflater.from(requireContext()))
        val data: Story = marker.tag as Story
        bindingTooltips.labelLocation.text = Helper.getStoryLocation(
            requireContext(),
            marker.position.latitude, marker.position.longitude
        )
        bindingTooltips.name.text = "Story by ${data.name}"
        bindingTooltips.image.setImageBitmap(Helper.bitmapFromURL(requireContext(), data.photoUrl))
        bindingTooltips.storyDescription.text = data.description
        bindingTooltips.storyUploadTime.text = Helper.getUploadStoryTime(data.createdAt)
        return bindingTooltips.root
    }

    override fun getInfoContents(marker: Marker): View? {
        return null
    }


}

