package com.joseg.fakeyouclient.network.model

data class NetworkVoiceModel(
    val model_token: String,
    val tts_model_type: String,
    val creator_user_token: String,
    val creator_username: String,
    val creator_display_name: String,
    val creator_gravatar_hash: String,
    val title: String,
    val ietf_language_tag: String,
    val ietf_primary_language_subtag: String,
    val is_front_page_featured: Boolean,
    val is_twitch_featured: Boolean,
    val maybe_suggested_unique_bot_command: String?,
    val creator_set_visibility: String,
    val user_ratings: NetworkUserRatings,
    val category_tokens: List<String>,
    val created_at: String,
    val updated_at: String
)

data class NetworkVoiceModels(
    val success: Boolean,
    val models: List<NetworkVoiceModel>
)

data class NetworkUserRatings(
    val positive_count: Int,
    val negative_count: Int,
    val total_count: Int
)
