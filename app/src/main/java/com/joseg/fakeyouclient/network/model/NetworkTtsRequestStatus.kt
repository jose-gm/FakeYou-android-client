package com.joseg.fakeyouclient.network.model

data class NetworkTtsRequestStatus(
    val success: Boolean,
    val state: NetworkTtsRequestState
)

data class NetworkTtsRequestState(
    val job_token: String,
    val status: String,
    val maybe_extra_status_description: String?,
    val attempt_count: Int,
    val maybe_result_token: String?,
    val maybe_public_bucket_wav_audio_path: String?,
    val model_token: String,
    val tts_model_type: String,
    val title: String,
    val raw_inference_text: String,
    val created_at: String,
    val updated_at: String
)
