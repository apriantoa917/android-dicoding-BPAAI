package com.aprianto.dicostory.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.net.toUri
import com.aprianto.dicostory.R
import com.aprianto.dicostory.ui.dashboard.profile.WebViewActivity


/**
 * Implementation of App Widget functionality.
 */
class RecentStoryWidget : AppWidgetProvider() {
    companion object {

        private const val WEBVIEW_ACTION = "WEBVIEW_ACTION"
        const val EXTRA_ITEM = "EXTRA_ITEM"

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

            val toastIntent = Intent(context, RecentStoryWidget::class.java)
            toastIntent.action = WEBVIEW_ACTION
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
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action != null) {
            if (intent.action == WEBVIEW_ACTION) {
                val webviewIntent = Intent(context, WebViewActivity::class.java)
                webviewIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                webviewIntent.putExtra(WebViewActivity.EXTRA_WEBVIEW, "https://www.dicoding.com/about")
                context.startActivity(webviewIntent)
            }
        }
    }
}