package com.aprianto.dicostory.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aprianto.dicostory.data.model.Story
import com.aprianto.dicostory.data.repository.room.StoryRepository

class StoryPagerViewModel(storyRepository: StoryRepository) : ViewModel() {
    val story: LiveData<PagingData<Story>> =
        storyRepository.getStory().cachedIn(viewModelScope)
}
