package com.joseg.fakeyouclient.datastore.cache.notification

import androidx.annotation.DrawableRes

data class NotificationState(
    val notificationId: String,
    val workerUuId: String?,
    val status: String?,
    val title: String,
    val content: String,
    @DrawableRes val icon: Int,
    val retryWork: Boolean
)
