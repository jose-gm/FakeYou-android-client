package com.joseg.fakeyouclient.model

data class VoiceModel(
    val modelToken: String,
    val ttsModelType: String,
    val creatorUserToken: String,
    val creatorUsername: String,
    val creatorDisplayName: String,
    val creatorGravatarHash: String,
    val title: String,
    val ietfLanguageTag: String,
    val ietfPrimaryLanguageSubtag: String,
    val isFrontPageFeatured: Boolean,
    val isTwitchFeatured: Boolean,
    val maybeSuggestedUniqueBotCommand: String,
    val categoryTokens: List<String>,
    val createdAt: String,
    val updatedAt: String
)

data class VoiceModelCompact(
    val modelToken: String,
    val ttsModelType: String,
    val creatorDisplayName: String,
    val title: String,
    val ietfPrimaryLanguageSubtag: String,
    val categoryTokens: List<String>
)
