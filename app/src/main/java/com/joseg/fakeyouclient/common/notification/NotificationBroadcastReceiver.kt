package com.joseg.fakeyouclient.common.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.joseg.fakeyouclient.data.worker.TtsRequestNotificationManager
import com.joseg.fakeyouclient.data.worker.TtsRequestPollerWorker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationBroadcastReceiver : BroadcastReceiver() {
    @Inject lateinit var ttsRequestNotificationManager: TtsRequestNotificationManager

    companion object {
        const val ACTION_REMOVE_STATE = "remove_state"
        const val ACTION_RETRY = "retry"

        const val NOTIFICATION_ID_INTENT_KEY = "notificationId"
        const val VOICE_MODEL_NAME_INTENT_KEY = "voiceModelName"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getStringExtra(NOTIFICATION_ID_INTENT_KEY) ?: ""
        val voiceModelName = intent.getStringExtra(VOICE_MODEL_NAME_INTENT_KEY) ?: ""

        when (intent.action) {
            ACTION_REMOVE_STATE -> {
                ttsRequestNotificationManager.removeNotificationStateFromCache(notificationId)
                ttsRequestNotificationManager.updateNotifications()
            }
            ACTION_RETRY -> {
                ttsRequestNotificationManager.removeNotification(notificationId)
                ttsRequestNotificationManager.updateNotifications()
                TtsRequestPollerWorker.start(context, notificationId.split("-").first(), voiceModelName)
            }
        }
    }
}