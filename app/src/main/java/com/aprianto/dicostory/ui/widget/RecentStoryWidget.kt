package com.aprianto.dicostory.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import com.aprianto.dicostory.R
import com.aprianto.dicostory.ui.detail.DetailActivity
import com.aprianto.dicostory.utils.Constanta

class RecentStoryWidget : AppWidgetProvider() {
    companion object {

        private const val ITEMS_CLICK = "ITEMS_CLICK_FROM_LIST"
        const val BROADCAST_UPDATE = "RECEIVE_BROADCAST_UPDATES"

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, RecentStoryService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

            val views = RemoteViews(context.packageName, R.layout.widget_recent_story)
            views.setRemoteAdapter(R.id.stack_view, intent)

            /* if image items click -> forward to onRecieve*/
            val toastIntent = Intent(context, RecentStoryWidget::class.java)
            toastIntent.action = ITEMS_CLICK
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            val toastPendingIntent = PendingIntent.getBroadcast(
                context, 0, toastIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                else 0
            )

            views.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)
            views.setEmptyView(R.id.stack_view, R.id.stack_view)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action != null) {
            when (intent.action) {
                ITEMS_CLICK -> {
                    /* if items widget click -> open detail activity */
                    val bundle = intent.extras
                    bundle?.let { params ->
                        val detailIntent = Intent(context, DetailActivity::class.java)
                        detailIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        val extras = bundleOf(
                            Constanta.StoryDetail.UserName.name to params.get(Constanta.StoryDetail.UserName.name),
                            Constanta.StoryDetail.ImageURL.name to params.get(Constanta.StoryDetail.ImageURL.name),
                            Constanta.StoryDetail.Longitude.name to params.get(Constanta.StoryDetail.Longitude.name),
                            Constanta.StoryDetail.Latitude.name to params.get(Constanta.StoryDetail.Latitude.name),
                            Constanta.StoryDetail.ContentDescription.name to params.get(Constanta.StoryDetail.ContentDescription.name),
                            Constanta.StoryDetail.UploadTime.name to params.get(Constanta.StoryDetail.UploadTime.name),
                        )
                        detailIntent.putExtras(extras)
                        context.startActivity(detailIntent)
                    }
                }
            }
        }
    }
}