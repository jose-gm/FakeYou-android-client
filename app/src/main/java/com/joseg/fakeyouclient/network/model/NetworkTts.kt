package com.joseg.fakeyouclient.network.model

data class NetworkTtsRequestBody(
    val tts_model_token: String,
    val uuid_idempotency_token: String,
    val inference_text: String
)

data class NetworkTtsResponse(
    val success: Boolean,
    val inference_job_token: String
)