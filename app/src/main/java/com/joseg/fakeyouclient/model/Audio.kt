package com.joseg.fakeyouclient.model

import com.joseg.fakeyouclient.database.entity.AudioEntity

data class Audio(
    val id: String,
    val voiceModelName: String,
    val inferenceText: String,
    val url: String,
    val sample: IntArray,
    val createdAt: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Audio

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

fun Audio.asEntity() = AudioEntity(
    id = id,
    voiceModelName = voiceModelName,
    inferenceText = inferenceText,
    url = url,
    sample = sample,
    createdAt = createdAt
)