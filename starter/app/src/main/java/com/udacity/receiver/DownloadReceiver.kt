package com.udacity.receiver

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.udacity.R
import com.udacity.util.sendDownloadCompleteNotification

private const val TAG = "DownloadReceiver"

class DownloadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            DownloadManager.ACTION_DOWNLOAD_COMPLETE -> {
                Log.d(TAG, "Reached ACTION_DOWNLOAD_COMPLETE in DownloadReceiver")
                val downloadManager =
                    context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                Log.d(TAG, "DownloadId retrieved in DownloadReceiver: $id")

                val downloadQuery = DownloadManager.Query()
                    .setFilterById(id)
                val cursor = downloadManager.query(downloadQuery)

                cursor.use {
                    if (it.moveToFirst()) {
                        val columnIndex =
                            it.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        val status = it.getInt(columnIndex)
                        val columnFileName =
                            it.getColumnIndex(DownloadManager.COLUMN_TITLE)
                        val fileName = it.getString(columnFileName)

                        Log.d(TAG, "status retrieved in DownloadReceiver: $status")
                        Log.d(TAG, "fileName retrieved in DownloadReceiver: $fileName")

                        val notificationManager = context.let { it2 ->
                            ContextCompat.getSystemService(
                                it2,
                                NotificationManager::class.java
                            )
                        } as NotificationManager
                        notificationManager.sendDownloadCompleteNotification(
                            context,
                            context.getText(R.string.notification_description).toString(),
                            status,
                            fileName
                        )
                    }
                }
            }
        }
    }
}