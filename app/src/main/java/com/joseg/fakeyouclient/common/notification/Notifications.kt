package com.joseg.fakeyouclient.common.notification

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.joseg.fakeyouclient.R

object Notifications {
    const val TTS_REQUEST_STATUS_CHANNEL_GROUP = "tts_request_status_channel_group"
    const val TTS_REQUEST_STATUS_CHANNEL = "tts_request_status_channel"
    const val TTS_REQUEST_STATUS_CHANNEL_COMPLETE = "tts_request_status_channel_complete"
    const val TTS_REQUEST_STATUS_CHANNEL_FAILED = "tts_request_status_channel_failed"
    const val TTS_REQUEST_STATUS_GROUP_KEY = "tts_request_status_group_key"
    const val TTS_REQUEST_STATUS_GROUP_SUMMARY_ID = 101
    const val TTS_REQUEST_STATUS_TAG = "tts_request_status_tag"

    private val channelsToDelete = listOf(
        "download_progress_channel",
        "download_completed_channel",
        "download_failed_channel"
    )

    fun createNotificationChannels(context: Context) {
        val notificationManagerCompat = NotificationManagerCompat.from(context)

        notificationManagerCompat.createNotificationChannelGroupsCompat(
            listOf(
                NotificationChannelGroupCompat.Builder(TTS_REQUEST_STATUS_CHANNEL_GROUP)
                    .setName(context.getString(R.string.notification_group_tts_request))
                    .build()
            )
        )

        notificationManagerCompat.createNotificationChannelsCompat(
            listOf(
                NotificationChannelCompat.Builder(TTS_REQUEST_STATUS_CHANNEL, NotificationManagerCompat.IMPORTANCE_LOW)
                    .setName(context.getString(R.string.notification_channel_tts_request_status))
                    .setShowBadge(false)
                    .setGroup(TTS_REQUEST_STATUS_CHANNEL_GROUP)
                    .build(),
                NotificationChannelCompat.Builder(TTS_REQUEST_STATUS_CHANNEL_COMPLETE, NotificationManagerCompat.IMPORTANCE_LOW)
                    .setName(context.getString(R.string.notification_channel_tts_request_status))
                    .setShowBadge(false)
                    .setGroup(TTS_REQUEST_STATUS_CHANNEL_GROUP)
                    .build()
            )
        )

        channelsToDelete.forEach(notificationManagerCompat::deleteNotificationChannel)
        notificationManagerCompat.deleteNotificationChannelGroup("download_channel_group")
    }

    fun buildNotificationAndDisplay(
        context: Context,
        id: Int,
        channelId: String,
        builder: NotificationCompat.Builder.() -> Unit) {
        val notificationManager = NotificationManagerCompat.from(context)
        if (ContextCompat
            .checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
            return
        }
        notificationManager.notify(
            id,
            NotificationCompat.Builder(context, channelId)
                .apply(builder)
                .build()
        )
    }

    fun buildNotification(
        context: Context,
        channelId: String,
        builder: NotificationCompat.Builder.() -> Unit): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId)
            .apply(builder)
    }

    fun buildNotificationsAndDisplay(
        context: Context,
        list: List<Pair<Int,NotificationCompat.Builder>>
    ) {
        val notificationManager = NotificationManagerCompat.from(context)
        if (ContextCompat
                .checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
            return
        }

        list.forEach {
            notificationManager.notify(
                it.first,
                it.second.build()
            )
        }
    }

    fun removeNotification(context: Context, id: Int) {
        NotificationManagerCompat.from(context).cancel(id)
    }

    fun removeNotifications(context: Context, list: List<Int>) {
        val notificationManager = NotificationManagerCompat.from(context)
        list.forEach {
            notificationManager.cancel(it)
        }
    }

    fun removeAll(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancelAll()
    }

    fun getNotifications(context: Context): Array<StatusBarNotification> {
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.activeNotifications
    }
}