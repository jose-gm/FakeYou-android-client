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
    @ColumnInfo("sample") val sample: IntArray,
    @ColumnInfo("created_at") val createdAt: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AudioEntity

        if (id != other.id) return false
        if (voiceModelName != other.voiceModelName) return false
        if (inferenceText != other.inferenceText) return false
        if (url != other.url) return false
        if (!sample.contentEquals(other.sample)) return false
        if (createdAt != other.createdAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + voiceModelName.hashCode()
        result = 31 * result + inferenceText.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + sample.contentHashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }
}

fun AudioEntity.asModel(): Audio = Audio(
    id = id,
    voiceModelName = voiceModelName,
    inferenceText = inferenceText,
    url = url,
    sample = sample,
    createdAt = createdAt
)