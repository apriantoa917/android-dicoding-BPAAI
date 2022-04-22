package com.aprianto.dicostory.ui.detail

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.aprianto.dicostory.databinding.ActivityDetailBinding
import com.aprianto.dicostory.utils.Constanta
import com.aprianto.dicostory.utils.Helper
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

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

        try {
            val lat = intent.getData(Constanta.StoryDetail.Latitude.name)
            val lon = intent.getData(Constanta.StoryDetail.Longitude.name)
            binding.labelLocation.text =
                Helper.getStoryLocation(this, lat.toDouble(), lon.toDouble())
            binding.labelLocation.isVisible = true
        } catch (e: Exception) {
            binding.labelLocation.isVisible = false
//            binding.labelLocation.text = "📌 Location Unknown"
        }
    }

    private fun Intent.getData(key: String, defaultValue: String = "None"): String {
        return getStringExtra(key) ?: defaultValue
    }

}