package com.joseg.fakeyouclient.ui

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.joseg.fakeyouclient.common.notification.Notifications
import com.joseg.fakeyouclient.datastore.cache.notification.NotificationStateCache
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FakeYouApplication : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var notificationStateCache: NotificationStateCache

    override fun onCreate() {
        super.onCreate()

        Notifications.createNotificationChannels(this)
        if (Notifications.getNotifications(this).isEmpty())
            notificationStateCache.removeAll()
    }

    override fun getWorkManagerConfiguration(): Configuration = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()

}