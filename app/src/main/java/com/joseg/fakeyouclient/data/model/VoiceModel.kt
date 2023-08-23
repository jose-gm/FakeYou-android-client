package com.joseg.fakeyouclient.data.model

import com.joseg.fakeyouclient.common.enums.LanguageTag
import com.joseg.fakeyouclient.model.UserRatings
import com.joseg.fakeyouclient.model.VoiceModel
import com.joseg.fakeyouclient.model.VoiceModelCompact
import com.joseg.fakeyouclient.network.model.NetworkVoiceModel
import com.joseg.fakeyouclient.network.model.NetworkVoiceModels

fun NetworkVoiceModel.asVoiceModel() = VoiceModel(
    modelToken = model_token,
    ttsModelType = tts_model_type,
    creatorUserToken = creator_user_token,
    creatorUsername = creator_username,
    creatorDisplayName = creator_display_name,
    creatorGravatarHash = creator_gravatar_hash,
    title = title,
    ietfLanguageTag = ietf_language_tag,
    ietfPrimaryLanguageSubtag = ietf_primary_language_subtag,
    isFrontPageFeatured = is_front_page_featured,
    isTwitchFeatured = is_twitch_featured,
    maybeSuggestedUniqueBotCommand = maybe_suggested_unique_bot_command,
    userRatings = UserRatings(
        user_ratings.positive_count,
        user_ratings.negative_count,
        user_ratings.total_count
    ),
    categoryTokens = category_tokens,
    createdAt = created_at,
    updatedAt = updated_at
)

fun NetworkVoiceModel.asVoiceModelCompact() = VoiceModelCompact(
    modelToken = model_token,
    ttsModelType = tts_model_type,
    creatorDisplayName = creator_display_name,
    title = title,
    ietfPrimaryLanguageSubtag = LanguageTag.parse(ietf_primary_language_subtag),
    userRatings = UserRatings(
        user_ratings.positive_count,
        user_ratings.negative_count,
        user_ratings.total_count
    ),
    categoryTokens = category_tokens
)

fun NetworkVoiceModels.asVoiceModelsCompact(): List<VoiceModelCompact> =
    models.map {
        it.asVoiceModelCompact()
    }

fun List<NetworkVoiceModel>.asVoiceModelsCompact(): List<VoiceModelCompact> = this.map { it.asVoiceModelCompact() }