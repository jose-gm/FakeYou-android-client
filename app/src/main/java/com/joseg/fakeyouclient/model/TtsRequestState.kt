package com.joseg.fakeyouclient.model

import com.joseg.fakeyouclient.common.enums.TtsRequestStatusType

data class TtsRequestState(
    val jobToken: String,
    val status: TtsRequestStatusType,
    val maybeExtraStatusDescription: String?,
    val attemptCount: Int,
    val maybeResultToken: String?,
    val maybePublicBucketWavAudioPath: String?,
    val modelToken: String,
    val ttsModelType: String,
    val title: String,
    val rawInferenceText: String,
    val createdAt: String,
    val updatedAt: String
)