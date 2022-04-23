package com.aprianto.dicostory.data.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aprianto.dicostory.data.database.StoryDatabase
import com.aprianto.dicostory.data.repository.remote.ApiConfig
import com.aprianto.dicostory.data.repository.remote.ApiService
import com.aprianto.dicostory.data.repository.remote.storypaging.StoryRepository


class ViewModelStoryFactory(val context: Context, private val apiService: ApiService) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryPagerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val database = StoryDatabase.getDatabase(context)
            return StoryPagerViewModel(
                com.aprianto.dicostory.data.repository.room.StoryRepository(
                    database,
                    apiService
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}