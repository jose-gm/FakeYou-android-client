package com.joseg.fakeyouclient.data.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.PendingIntentCompat
import androidx.work.WorkManager
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.common.enums.TtsRequestStatusType
import com.joseg.fakeyouclient.common.notification.NotificationBroadcastReceiver
import com.joseg.fakeyouclient.common.notification.Notifications
import com.joseg.fakeyouclient.datastore.cache.notification.NotificationState
import com.joseg.fakeyouclient.datastore.cache.notification.NotificationStateCache
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class TtsRequestNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationStateCache: NotificationStateCache
) {
    private val workManager = WorkManager.getInstance(context)

    fun submitNotification(
        notificationId: String,
        title: String,
        status: TtsRequestStatusType,
        workerId: String
    ) {
        var content: String
        var newNotificationId: String?
        @DrawableRes var icon: Int

        when (status) {
            TtsRequestStatusType.PENDING -> {
                content = context.getString(R.string.notification_tts_request_status_content_pending)
                newNotificationId = notificationId
                icon = R.drawable.ic_logo_temp
            }
            TtsRequestStatusType.STARTED -> {
                content = context.getString(R.string.notification_tts_request_status_content_started)
                newNotificationId = notificationId
                icon = R.drawable.ic_logo_temp
            }
            TtsRequestStatusType.ATTEMPT_FAILED -> {
                content = context.getString(R.string.notification_tts_request_status_content_attempt_failed)
                newNotificationId = notificationId
                icon = R.drawable.ic_logo_temp
            }
            TtsRequestStatusType.COMPLETE_SUCCESS -> {
                content = context.getString(R.string.notification_tts_request_status_content_completed)
                newNotificationId = (notificationId + "-" + Notifications.TTS_REQUEST_STATUS_CHANNEL_COMPLETE)
                icon = R.drawable.ic_baseline_check
            }
            else -> {
                content = context.getString(R.string.notification_tts_request_status_content_dead_or_complete_failure)
                newNotificationId = (notificationId + "-" + Notifications.TTS_REQUEST_STATUS_CHANNEL_FAILED)
                icon = R.drawable.ic_warning
            }
        }

        notificationStateCache.addOrUpdate(
            NotificationState(
                notificationId = newNotificationId,
                workerUuId = workerId,
                status = status.name,
                title = title,
                content = content,
                icon = icon,
                retryWork = false
            )
        )
    }

    fun removeNotification(id: String) {
        removeNotificationStateFromCache(id)
        Notifications.removeNotification(context, id.hashCode())
    }

    fun removeNotificationStateFromCache(id: String) {
        notificationStateCache.remove(id)
    }

    fun removeNotificationsWhenFinished() {
        if (!notificationStateCache.getAll().any { notificationState ->
                val status = notificationState.status
                status?.let { TtsRequestStatusType.parse(it) } == TtsRequestStatusType.PENDING ||
                        status?.let { TtsRequestStatusType.parse(it) } == TtsRequestStatusType.STARTED ||
                        status?.let { TtsRequestStatusType.parse(it) } == TtsRequestStatusType.ATTEMPT_FAILED })
            notificationStateCache.removeAll()
    }

    fun updateNotifications() {
        val notificationStateQueue = notificationStateCache.getAll()

        CoroutineScope(Dispatchers.Main).launch {
            if (notificationStateQueue.isEmpty()) {
                Notifications.removeAll(context)
                notificationStateCache.removeAll()
            } else if (notificationStateQueue.size == 1)  {
                val notificationState = notificationStateQueue.firstOrNull()
                val status = notificationState?.status ?: ""

                Notifications.buildNotificationAndDisplay(
                    context,
                    notificationState?.notificationId.hashCode(),
                    Notifications.TTS_REQUEST_STATUS_CHANNEL,
                ) {
                    setContentTitle(notificationState?.title)
                    setContentText(notificationState?.content)
                    setSound(null)
                    setGroup(null)
                    setSmallIcon(notificationState?.icon ?: R.drawable.ic_logo_temp)
                    if (TtsRequestStatusType.parse(status) == TtsRequestStatusType.PENDING ||
                        TtsRequestStatusType.parse(status) == TtsRequestStatusType.STARTED ||
                        TtsRequestStatusType.parse(status) == TtsRequestStatusType.ATTEMPT_FAILED)
                        addAction(
                            0,
                            context.getString(R.string.notification_action_cancel),
                            workManager.createCancelPendingIntent(UUID.fromString(notificationState?.workerUuId)))
                    if (notificationState?.retryWork == true)
                        addAction(
                            0,
                            context.getString(R.string.notification_action_retry),
                            createRetryPendingIntent(context, notificationState.notificationId, notificationState.title)
                        )
                    if (TtsRequestStatusType.parse(status) == TtsRequestStatusType.STARTED)
                        setProgress(0, 0, true)
                    if (TtsRequestStatusType.parse(status) == TtsRequestStatusType.PENDING ||
                        TtsRequestStatusType.parse(status) == TtsRequestStatusType.STARTED ||
                        TtsRequestStatusType.parse(status) == TtsRequestStatusType.ATTEMPT_FAILED)
                        setOngoing(true)
                    setDeleteIntent(createRemoveStatePendingIntent(context, notificationState?.notificationId ?: ""))
                }

                // Remove summary notification
                Notifications.removeNotification(context, Notifications.TTS_REQUEST_STATUS_GROUP_SUMMARY_ID)

            } else if (notificationStateQueue.size >= 2) {
                // Create/Update summary notification
                Notifications.buildNotificationAndDisplay(
                    context,
                    Notifications.TTS_REQUEST_STATUS_GROUP_SUMMARY_ID,
                    Notifications.TTS_REQUEST_STATUS_CHANNEL
                ) {
                    setContentTitle(context.getString(R.string.notification_tts_request_status_group_summary_title))
                    setSmallIcon(R.drawable.ic_logo_temp)
                    setSound(null)
                    setGroup(Notifications.TTS_REQUEST_STATUS_GROUP_KEY)
                    setGroupSummary(true)
                    setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                    priority = NotificationCompat.PRIORITY_HIGH
                    if (notificationStateQueue.any { notificationState ->
                            val status = notificationState.status ?: ""
                            TtsRequestStatusType.parse(status) == TtsRequestStatusType.PENDING ||
                                    TtsRequestStatusType.parse(status) == TtsRequestStatusType.STARTED ||
                                    TtsRequestStatusType.parse(status) == TtsRequestStatusType.ATTEMPT_FAILED
                        })
                        setOngoing(true)
                }

                // Update all notifications in the state cache
                val list = notificationStateQueue.map { notificationState ->
                    val status = notificationState.status ?: ""

                    Pair(
                        notificationState.notificationId.hashCode(),
                        Notifications.buildNotification(context, Notifications.TTS_REQUEST_STATUS_CHANNEL) {
                            setContentTitle(notificationState.title)
                            setContentText(notificationState.content)
                            setGroup(Notifications.TTS_REQUEST_STATUS_GROUP_KEY)
                            setSmallIcon(notificationState.icon)
                            setSound(null)
                            priority = NotificationCompat.PRIORITY_HIGH
                            if (TtsRequestStatusType.parse(status) == TtsRequestStatusType.PENDING ||
                                TtsRequestStatusType.parse(status) == TtsRequestStatusType.STARTED ||
                                TtsRequestStatusType.parse(status) == TtsRequestStatusType.ATTEMPT_FAILED)
                                addAction(
                                    0,
                                    context.getString(R.string.notification_action_cancel),
                                    workManager.createCancelPendingIntent(UUID.fromString(notificationState.workerUuId)))
                            if (notificationState.retryWork)
                                addAction(
                                    0,
                                    context.getString(R.string.notification_action_retry),
                                    createRetryPendingIntent(context, notificationState.notificationId, notificationState.title)
                                )
                            if (TtsRequestStatusType.parse(status) == TtsRequestStatusType.STARTED)
                                setProgress(0, 0, true)
                            if (TtsRequestStatusType.parse(status) == TtsRequestStatusType.PENDING ||
                                TtsRequestStatusType.parse(status) == TtsRequestStatusType.STARTED ||
                                TtsRequestStatusType.parse(status) == TtsRequestStatusType.ATTEMPT_FAILED)
                                setOngoing(true)
                            setDeleteIntent(createRemoveStatePendingIntent(context, notificationState.notificationId))
                        }
                    )
                }
                Notifications.buildNotificationsAndDisplay(context, list)

            }
        }
    }

    fun submitRetryNotification(notificationId: String, title: String) {
        notificationStateCache.addOrUpdate(
            NotificationState(
                notificationId = "$notificationId-retryWork",
                workerUuId = null,
                status = null,
                title = title,
                content = context.getString(R.string.notification_tts_request_status_content_retry_work),
                icon = R.drawable.ic_warning,
                retryWork = true
            )
        )
        removeNotificationStateFromCache(notificationId)
    }

    private fun createRemoveStatePendingIntent(context: Context, notificationId: String): PendingIntent {
        val intent = Intent(context, NotificationBroadcastReceiver::class.java).apply {
            action = NotificationBroadcastReceiver.ACTION_REMOVE_STATE
            putExtra(NotificationBroadcastReceiver.NOTIFICATION_ID_INTENT_KEY, notificationId)
        }
        return PendingIntentCompat.getBroadcast(
            context,
            notificationId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT,
            false
        )
    }

    private fun createRetryPendingIntent(context: Context, notificationId: String, voiceModelName: String): PendingIntent {
        val intent = Intent(context, NotificationBroadcastReceiver::class.java).apply {
            action = NotificationBroadcastReceiver.ACTION_RETRY
            putExtra(NotificationBroadcastReceiver.NOTIFICATION_ID_INTENT_KEY, notificationId)
            putExtra(NotificationBroadcastReceiver.VOICE_MODEL_NAME_INTENT_KEY, voiceModelName)
        }
        return PendingIntentCompat.getBroadcast(
            context,
            notificationId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT,
            false
        )
    }
}