package de.aaronoe.picsplash.receivers

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.aaronoe.picsplash.util.PhotoDownloadUtils

/**
 * Created by aaron on 05.06.17.
 *
 */

class DownloadFileReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
        PhotoDownloadUtils.sendNotificationForId(context.applicationContext, downloadId)
    }
}