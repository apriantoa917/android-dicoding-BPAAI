package com.aprianto.dicostory.ui.widget

import android.content.Intent
import android.widget.RemoteViewsService

class RecentStoryService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory =
        StackRemoteViewsFactory(this.applicationContext)
}