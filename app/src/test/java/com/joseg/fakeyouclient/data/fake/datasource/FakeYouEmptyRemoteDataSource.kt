package com.joseg.fakeyouclient.data.fake.datasource

import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import com.joseg.fakeyouclient.network.model.NetworkCategory
import com.joseg.fakeyouclient.network.model.NetworkTtsRequestBody
import com.joseg.fakeyouclient.network.model.NetworkTtsRequestState
import com.joseg.fakeyouclient.network.model.NetworkVoiceModel

class FakeYouEmptyRemoteDataSource :
    FakeYouRemoteDataSource {
    override suspend fun getVoiceModels(): List<NetworkVoiceModel> = emptyList()

    override suspend fun getCategories(): List<NetworkCategory>  = emptyList()

    override suspend fun posTtsRequest(networkTtsRequestBody: NetworkTtsRequestBody): String = ""

    override suspend fun getTtsRequestState(inferenceToken: String): NetworkTtsRequestState = NetworkTtsRequestState(
        job_token = "",
        status = "",
        maybe_extra_status_description = "",
        attempt_count = 0,
        maybe_result_token = "",
        maybe_public_bucket_wav_audio_path = "",
        model_token = "",
        tts_model_type = "",
        title = "",
        raw_inference_text = "",
        created_at = "",
        updated_at = ""
    )
}