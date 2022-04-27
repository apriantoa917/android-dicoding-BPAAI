package com.aprianto.dicostory.data.viewmodel

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aprianto.dicostory.R
import com.aprianto.dicostory.data.model.Story
import com.aprianto.dicostory.data.model.StoryList
import com.aprianto.dicostory.data.model.StoryUpload
import com.aprianto.dicostory.data.repository.remote.ApiConfig
import com.aprianto.dicostory.utils.Constanta
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class StoryViewModel : ViewModel() {
    val loading = MutableLiveData(View.GONE)
    val isSuccessUploadStory = MutableLiveData(false)
    val storyList = MutableLiveData<List<Story>>()
    val error = MutableLiveData("")
    val isError = MutableLiveData(true)


    val isLocationPicked = MutableLiveData(false) // init for location new story not selected
    val coordinateLatitude = MutableLiveData(0.0)
    val coordinateLongitude = MutableLiveData(0.0)
    val coordinateTemp = MutableLiveData(Constanta.indonesiaLocation)

    private val TAG = StoryViewModel::class.simpleName

    fun loadStoryLocationData(context: Context, token: String) {
        val client = ApiConfig.getApiService().getStoryListLocation(token, 100)
        client.enqueue(object : Callback<StoryList> {
            override fun onResponse(call: Call<StoryList>, response: Response<StoryList>) {
                if (response.isSuccessful) {
                    isError.postValue(false)
                    storyList.postValue(response.body()?.listStory)
                } else {
                    isError.postValue(true)
                    error.postValue("ERROR ${response.code()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryList>, t: Throwable) {
                loading.postValue(View.GONE)
                isError.postValue(true)
                Log.e(TAG, "onFailure Call: ${t.message}")
                error.postValue("${context.getString(R.string.API_error_fetch_data)} : ${t.message}")
            }
        })
    }

    fun uploadNewStory(
        context: Context,
        token: String,
        image: File,
        description: String,
        withLocation: Boolean = false,
        lat: String? = null,
        lon: String? = null
    ) {
        loading.postValue(View.VISIBLE)
        "${image.length() / 1024 / 1024} MB" // manual parse from bytes to Mega Bytes
        val storyDescription = description.toRequestBody("text/plain".toMediaType())

        val requestImageFile = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            image.name,
            requestImageFile
        )
        val client = if (withLocation) {
            val positionLat = lat?.toRequestBody("text/plain".toMediaType())
            val positionLon = lon?.toRequestBody("text/plain".toMediaType())
            ApiConfig.getApiService()
                .doUploadImage(
                    token,
                    imageMultipart,
                    storyDescription,
                    positionLat!!,
                    positionLon!!
                )
        } else {
            ApiConfig.getApiService()
                .doUploadImage(token, imageMultipart, storyDescription)
        }
        client.enqueue(object : Callback<StoryUpload> {
            override fun onResponse(call: Call<StoryUpload>, response: Response<StoryUpload>) {
                when (response.code()) {
                    401 -> error.postValue(context.getString(R.string.API_error_header_token))
                    413 -> error.postValue(context.getString(R.string.API_error_large_payload))
                    201 -> isSuccessUploadStory.postValue(true)
                    else -> error.postValue("Error ${response.code()} : ${response.message()}")
                }
                loading.postValue(View.GONE)
            }

            override fun onFailure(call: Call<StoryUpload>, t: Throwable) {
                loading.postValue(View.GONE)
                Log.e(TAG, "onFailure Call: ${t.message}")
                error.postValue("${context.getString(R.string.API_error_send_payload)} : ${t.message}")
            }
        })
    }


}