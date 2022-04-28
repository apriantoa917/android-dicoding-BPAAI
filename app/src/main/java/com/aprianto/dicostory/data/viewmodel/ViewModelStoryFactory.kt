@file:Suppress("UNCHECKED_CAST")

package com.aprianto.dicostory.data.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aprianto.dicostory.data.database.StoryDatabase
import com.aprianto.dicostory.data.repository.remote.ApiService


class ViewModelStoryFactory(val context: Context, private val apiService: ApiService, val token:String) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryPagerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val database = StoryDatabase.getDatabase(context)
            return StoryPagerViewModel(
                com.aprianto.dicostory.data.repository.room.StoryRepository(
                    database,
                    apiService, token
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}