package com.aprianto.dicostory.ui.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.Toast
import androidx.core.os.bundleOf
import com.aprianto.dicostory.R
import com.aprianto.dicostory.data.model.Story
import com.aprianto.dicostory.data.repository.remote.ApiConfig
import com.aprianto.dicostory.utils.Constanta
import com.aprianto.dicostory.utils.Helper
import kotlinx.coroutines.runBlocking


internal class StackRemoteViewsFactory(private val mContext: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = ArrayList<Bitmap>()
    private val mDataItems = ArrayList<Story>()

    override fun onCreate() {
        Handler(mContext.mainLooper).post {
            Toast.makeText(
                mContext,
                "Mohon menunggu widget dimuat",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDataSetChanged() {
        Log.i("TEST_WIDGET", "Requested to request data")
        runBlocking {
            try {
                val service = ApiConfig.getApiService()
                val response = service.getStoryListWidget(Constanta.tempToken, 10).body()
                val stories = response?.listStory
                Log.i("TEST_WIDGET", "Fetched Stories : $stories")
                if (stories != null) {
                    mWidgetItems.clear()
                    mDataItems.clear()
                    for (story in stories) {
                        val bitmap = Helper.bitmapFromURL(mContext, story.photoUrl)
                        val newBitmap = Helper.getResizedBitmap(bitmap, 500, 500)
                        mWidgetItems.add(newBitmap)
                        mDataItems.add(story)
                    }
                } else {
                    Log.i("TEST_WIDGET", "Empty Stories")
                }
            } catch (exception: Exception) {
                Handler(mContext.mainLooper).post {
                    Toast.makeText(
                        mContext,
                        "Failed fetch data : ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("TEST_WIDGET", "Failed fetch data : ${exception.message}")
                    exception.printStackTrace()
                }
            }
        }
    }


    override fun onDestroy() {
    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[position])
        val extras = bundleOf(
            Constanta.StoryDetail.UserName.name to mDataItems[position].name,
            Constanta.StoryDetail.ImageURL.name to mDataItems[position].photoUrl,
            Constanta.StoryDetail.Longitude.name to mDataItems[position].lon,
            Constanta.StoryDetail.Latitude.name to mDataItems[position].lat,
            Constanta.StoryDetail.ContentDescription.name to mDataItems[position].description,
            Constanta.StoryDetail.UploadTime.name to Helper.getUploadStoryTime(mDataItems[position].createdAt),
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}