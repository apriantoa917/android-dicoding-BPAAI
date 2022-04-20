package com.aprianto.dicostory.data.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aprianto.dicostory.data.model.Story
import com.aprianto.dicostory.data.repository.remote.ApiConfig
import com.aprianto.dicostory.data.repository.remote.StoryPaging.StoryRepository

class StoryPagerViewModel(storyRepository: StoryRepository) : ViewModel() {
    val story: LiveData<PagingData<Story>> =
        storyRepository.getStoryList().cachedIn(viewModelScope)
}
