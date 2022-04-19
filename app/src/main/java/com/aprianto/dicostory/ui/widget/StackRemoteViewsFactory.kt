package com.aprianto.dicostory.ui.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.aprianto.dicostory.R
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


internal class StackRemoteViewsFactory(private val mContext: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = ArrayList<Bitmap>()
    override fun onCreate() {

    }

    override fun onDataSetChanged() {
        /* saya belum paham untuk ambil widget dari response API (sudah +/- 5 jam gak ketemu caranya) */
        mWidgetItems.add(bitmapFromURL("https://scontent.fsub4-1.fna.fbcdn.net/v/t31.18172-8/11118996_1466131763688678_6029409152114180180_o.png?_nc_cat=111&ccb=1-5&_nc_sid=174925&_nc_eui2=AeH9flq5fclJPkV3ZkZR03VTrVN4qfqykhKtU3ip-rKSElwm4ZnB5RmnDmffhunxV9x6EKH0ZWRB4i5yA8jI0PZX&_nc_ohc=zA5hODTZTT8AX_fQxda&_nc_ht=scontent.fsub4-1.fna&oh=00_AT9Nq8wl7hDnKVILBIgP0GoHTIkbmPb_H-RVkshrS1vdFQ&oe=62843C78"))
        mWidgetItems.add(bitmapFromURL("https://scontent.fsub3-2.fna.fbcdn.net/v/t1.6435-9/40307893_1918391288462721_2927640877947944960_n.png?_nc_cat=109&ccb=1-5&_nc_sid=174925&_nc_eui2=AeETg9cB416hXexVw_jCU0iAaEY2Q_oVcCxoRjZD-hVwLJcRtcziKda5DX7HZXdG8bFcsLDCnOGwhTF3bpbFXTAJ&_nc_ohc=lZdTeqVbWhwAX8PyN57&tn=_56k6CMIqi7k3mAt&_nc_ht=scontent.fsub3-2.fna&oh=00_AT-OBwSxET9UFb59_sdb40jjufrytQiwc5p-6DOw6cglfA&oe=6281EAC6"))
        mWidgetItems.add(bitmapFromURL("https://scontent.fsub4-1.fna.fbcdn.net/v/t1.6435-9/43599420_1938246306477219_3387385034989109248_n.png?_nc_cat=100&ccb=1-5&_nc_sid=174925&_nc_eui2=AeFGgnGONKCu-6TXYsn1Txyo7WhYoley54ntaFiiV7LniXMSw7oT_3r0bzJFsOYc5cvkLOVYv82xcDTqWlJNwGJr&_nc_ohc=q9ohnJwZezwAX-2yxFn&_nc_ht=scontent.fsub4-1.fna&oh=00_AT_WavgK258Df7sf1cjbD6hlRJoCA4sJezmwmKiECWdeWA&oe=6281BBE7"))
        mWidgetItems.add(bitmapFromURL("https://scontent.fsub3-2.fna.fbcdn.net/v/t1.6435-9/44259725_1941538346148015_376161894437748736_n.png?_nc_cat=104&ccb=1-5&_nc_sid=174925&_nc_eui2=AeGJ8wpoY1kiRmavqY9Akg_R9N-LFDhnnz_034sUOGefP_Npa5BSdDwO5spRuUbPep_rZgWhTV5nnYAghyjzkx0v&_nc_ohc=9NDgtBw6DtYAX_6M0Y8&_nc_ht=scontent.fsub3-2.fna&oh=00_AT-yfUm1vHyo-uEP2fT0mjvf4tXRGWr2SpdOh8GDOKLWOA&oe=6281AFC3"))
        mWidgetItems.add(bitmapFromURL("https://scontent.fsub3-1.fna.fbcdn.net/v/t1.6435-9/64689624_2078771672424681_1478911038496702464_n.jpg?_nc_cat=102&ccb=1-5&_nc_sid=174925&_nc_eui2=AeEvoQDTP8WhT5Arn9c9MoGrcCTztLOtmZFwJPO0s62Zkdjr1pee0cMGgWZsb_fe3lGUh7v0K7NKRQ5_vxKKj8-e&_nc_ohc=nqmF_jZX7UIAX-5MfPR&_nc_ht=scontent.fsub3-1.fna&oh=00_AT-AtjAn_9_xmrL80vRn5I3k5ys4rQZNaEu3KWuJ6ec_tg&oe=628358E2"))
    }

    private fun bitmapFromURL(urlString: String): Bitmap {
        return try {
            val url = URL(urlString)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            BitmapFactory.decodeResource(mContext.resources, R.drawable.bot)
        }
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[position])
        val extras = bundleOf(
            RecentStoryWidget.EXTRA_ITEM to position
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