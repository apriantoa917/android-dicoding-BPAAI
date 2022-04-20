package com.aprianto.dicostory.data.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aprianto.dicostory.data.repository.remote.ApiConfig
import com.aprianto.dicostory.data.repository.remote.ApiService
import com.aprianto.dicostory.data.repository.remote.StoryPaging.StoryRepository


class ViewModelStoryFactory(private val apiService: ApiService) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryPagerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoryPagerViewModel(StoryRepository(apiService)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}