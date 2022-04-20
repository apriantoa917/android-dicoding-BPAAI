package com.aprianto.dicostory.data.repository.remote.StoryPaging

import android.util.Log
import android.view.View
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aprianto.dicostory.R
import com.aprianto.dicostory.data.model.Story
import com.aprianto.dicostory.data.model.StoryList
import com.aprianto.dicostory.data.repository.remote.ApiConfig
import com.aprianto.dicostory.data.repository.remote.ApiService
import com.aprianto.dicostory.utils.Constanta
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryPagingSource(private val apiService: ApiService) :
    PagingSource<Int, Story>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX

            val data =
                apiService.getStoryList(Constanta.tempToken, position, params.loadSize).listStory
            Log.i("TESTING", "FETCHED DATA -> $data")
            LoadResult.Page(
                data = data,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (data.isNullOrEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            Log.e("TESTING", exception.message.toString())
            return LoadResult.Error(exception)
        }
    }

}