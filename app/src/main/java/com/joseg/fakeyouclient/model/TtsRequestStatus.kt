package com.joseg.fakeyouclient.model

data class TtsRequestState(
    val jobToken: String,
    val status: String,
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

data class TtsRequestStateCompact(
    val jobToken: String,
    val status: String,
    val maybePublicBucketWavAudioPath: String?,
    val title: String,
    val rawInferenceText: String
)
