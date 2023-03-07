package com.udacity.util

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.udacity.R

private const val NOTIFICATION_ID = 0

fun NotificationManager.sendDownloadCompleteNotification(
    applicationContext: Context,
    messageBody: String,
    downloadStatus: Int,
    fileName: String
) {
    val statusButtonPendingIntent = NavDeepLinkBuilder(applicationContext)
        .setGraph(R.navigation.main_nav_graph)
        .setDestination(R.id.detailFragment)
        .setArguments(
            bundleOf(
                "ACTION" to "CANCEL_NOTIFICATION",
                "downloadStatus" to downloadStatus,
                "fileName" to fileName
            )
        )
        .createPendingIntent()

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.download_notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(statusButtonPendingIntent)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.notification_button),
            statusButtonPendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}