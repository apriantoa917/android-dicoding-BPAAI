package com.aprianto.dicostory.ui.detail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.aprianto.dicostory.data.viewmodel.DetailViewModel
import com.aprianto.dicostory.databinding.ActivityDetailBinding
import com.aprianto.dicostory.utils.Constanta
import com.aprianto.dicostory.utils.Helper
import com.bumptech.glide.Glide
import kotlinx.coroutines.DelicateCoroutinesApi


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    val viewModel: DetailViewModel by viewModels()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* toolbar */
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.storyName.text =
            intent.getData(Constanta.StoryDetail.UserName.name, "Name")
        Glide.with(binding.root)
            .load(intent.getData(Constanta.StoryDetail.ImageURL.name, ""))
            .into(binding.storyImage)
        binding.storyDescription.text =
            intent.getData(Constanta.StoryDetail.ContentDescription.name, "Caption")
        binding.storyUploadTime.text =
            intent.getData(Constanta.StoryDetail.UploadTime.name, "Upload time")
        binding.btnDownload.setOnClickListener {
            if (Helper.isPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                val url = intent.getData(Constanta.StoryDetail.ImageURL.name, "")
                viewModel.saveImage(this, url)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Constanta.STORAGE_PERMISSION_CODE
                )
            }
        }

        viewModel.let { vm ->
            vm.isDownloading.observe(this) { isDownloading ->
                if (isDownloading == true) {
                    binding.loading.root.isVisible = true
                    binding.btnDownload.isVisible = false
                } else {
                    binding.loading.root.isVisible = false
                    binding.btnDownload.isVisible = true
                }

            }
            vm.error.observe(this) {
                if (it.isNotEmpty()) {
                    Helper.showDialogInfo(this, it)
                }
            }
        }

        try {
            val lat = intent.getData(Constanta.StoryDetail.Latitude.name)
            val lon = intent.getData(Constanta.StoryDetail.Longitude.name)
            binding.labelLocation.text =
                Helper.parseAddressLocation(this, lat.toDouble(), lon.toDouble())
            binding.labelLocation.isVisible = true
        } catch (e: Exception) {
            binding.labelLocation.isVisible = false
        }
    }

    private fun Intent.getData(key: String, defaultValue: String = "None"): String {
        return getStringExtra(key) ?: defaultValue
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constanta.STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Helper.notifyGivePermission(
                        this,
                        "Berikan aplikasi izin storage untuk menyimpan story"
                    )
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    }


}