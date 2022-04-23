package com.aprianto.dicostory.data.repository.room

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.aprianto.dicostory.data.database.StoryDatabase
import com.aprianto.dicostory.data.model.Story
import com.aprianto.dicostory.data.repository.remote.ApiService
import com.aprianto.dicostory.data.repository.remote.remotemediator.StoryRemoteMediator
import com.aprianto.dicostory.data.repository.remote.storypaging.StoryPagingSource

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    fun getStory(): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = { storyDatabase.storyDao().getAllStory() }
        ).liveData
    }
}