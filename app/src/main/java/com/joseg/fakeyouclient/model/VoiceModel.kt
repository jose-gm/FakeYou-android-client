package com.joseg.fakeyouclient.model

import com.joseg.fakeyouclient.common.enums.LanguageTag
import java.text.DecimalFormat

data class VoiceModel(
    val modelToken: String,
    val ttsModelType: String,
    val creatorUserToken: String,
    val creatorUsername: String,
    val creatorDisplayName: String,
    val creatorGravatarHash: String,
    val title: String,
    val ietfLanguageTag: String,
    val ietfPrimaryLanguageSubtag: LanguageTag,
    val isFrontPageFeatured: Boolean,
    val isTwitchFeatured: Boolean,
    val maybeSuggestedUniqueBotCommand: String?,
    val userRatings: UserRatings,
    val categoryTokens: List<String>,
    val createdAt: String,
    val updatedAt: String
)

data class UserRatings(
    val positiveCount: Int,
    val negativeCount: Int,
    val totalCount: Int
) {
    fun getFiveStarRatingScaleStringValue(): String {
        if (positiveCount <= 0)
            return "0.0"
        val rating = positiveCount.toDouble() / (totalCount.toDouble()) * 5.00
        val decimalFormat = DecimalFormat("#.#")
        return decimalFormat.format(rating)
    }
}

data class VoiceModelCompact(
    val modelToken: String,
    val ttsModelType: String,
    val creatorDisplayName: String,
    val title: String,
    val ietfPrimaryLanguageSubtag: LanguageTag,
    val userRatings: UserRatings,
    val categoryTokens: List<String>
)

fun VoiceModel.asVoiceModeCompact(): VoiceModelCompact = VoiceModelCompact(
    modelToken =  this.modelToken,
    ttsModelType = this.ttsModelType,
    creatorDisplayName = this.creatorDisplayName,
    title = this.title,
    ietfPrimaryLanguageSubtag = this.ietfPrimaryLanguageSubtag,
    userRatings = this.userRatings,
    categoryTokens = this.categoryTokens
)

fun List<VoiceModel>.asVoiceModelsCompact(): List<VoiceModelCompact> =
    this.map { it.asVoiceModeCompact() }
