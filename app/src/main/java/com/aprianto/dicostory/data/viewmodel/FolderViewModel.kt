package com.aprianto.dicostory.data.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aprianto.dicostory.data.model.Folder
import com.aprianto.dicostory.data.model.User
import com.aprianto.dicostory.utils.Helper
import java.io.File


class FolderViewModel : ViewModel() {

    val error = MutableLiveData("")

    val assetImageStory = MutableLiveData<ArrayList<Folder>>()
    val assetImageDownload = MutableLiveData<ArrayList<Folder>>()

    val loadingStory = MutableLiveData(true)
    val loadingDownload = MutableLiveData(true)

    /* async func to load when main activcity created */
    suspend fun loadImage(context: Context) {
        assetImageStory.postValue(fetchImageData(context))
        assetImageDownload.postValue(fetchImageData(context, mode = "download"))
    }

    private fun fetchImageData(context: Context, mode: String = "story"): ArrayList<Folder> {
        loadingState(mode, true)
        val folderData = ArrayList<Folder>()
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, mode).apply { mkdirs() }
        }
        val files = mediaDir?.listFiles()
        for (i in files!!.indices) {
            val path = "${mediaDir.absolutePath}/${files[i].name}"
            val bitmap =
                Helper.loadImageFromStorage(path)?.let { Helper.compressBitmap(it, 200, 200) }
            bitmap?.let {
                val folder = Folder(it, path)
                folderData.add(folder)
            }
        }
        folderData.reverse() // sort by recent files
        loadingState(mode, false)
        return folderData
    }

    private fun loadingState(mode: String, state: Boolean) {
        when (mode) {
            "story" -> loadingStory.postValue(state)
            "download" -> loadingDownload.postValue(state)
        }
    }
}