package com.joseg.fakeyouclient.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.joseg.fakeyouclient.model.Audio

@Entity(tableName = "Audio")
data class AudioEntity(
    @PrimaryKey val id: String,
    @ColumnInfo("voice_model_name") val voiceModelName: String,
    @ColumnInfo("inference_text") val inferenceText: String,
    @ColumnInfo("url") val url: String,
    @ColumnInfo("created_at") val createdAt: String
)

fun AudioEntity.asModel(): Audio = Audio(
    id = id,
    voiceModelName = voiceModelName,
    inferenceText = inferenceText,
    url = url,
    createdAt = createdAt
)