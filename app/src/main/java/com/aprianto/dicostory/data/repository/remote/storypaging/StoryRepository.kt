package com.aprianto.dicostory.data.repository.remote.storypaging

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.aprianto.dicostory.data.model.Story
import com.aprianto.dicostory.data.repository.remote.ApiService

class StoryRepository(private val apiService: ApiService) {
    fun getStoryList(): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = { StoryPagingSource(apiService) }
        ).liveData
    }
}