package com.joseg.fakeyouclient.model

import com.joseg.fakeyouclient.database.entity.AudioEntity

data class Audio(
    val id: String,
    val voiceModelName: String,
    val inferenceText: String,
    val url: String,
    val createdAt: String
)

fun Audio.asEntity() = AudioEntity(
    id = id,
    voiceModelName = voiceModelName,
    inferenceText = inferenceText,
    url = url,
    createdAt = createdAt
)