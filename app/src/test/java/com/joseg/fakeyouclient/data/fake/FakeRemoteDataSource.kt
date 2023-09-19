package com.joseg.fakeyouclient.data.fake

import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import com.joseg.fakeyouclient.network.model.NetworkCategories
import com.joseg.fakeyouclient.network.model.NetworkCategory
import com.joseg.fakeyouclient.network.model.NetworkTtsRequestBody
import com.joseg.fakeyouclient.network.model.NetworkTtsRequestState
import com.joseg.fakeyouclient.network.model.NetworkTtsRequestStatus
import com.joseg.fakeyouclient.network.model.NetworkTtsResponse
import com.joseg.fakeyouclient.network.model.NetworkVoiceModel
import com.joseg.fakeyouclient.network.model.NetworkVoiceModels
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer
import okio.source
import javax.inject.Inject

class FakeRemoteDataSource @Inject constructor(
    private val moshi: Moshi,
    private val fakeAssetManager: FakeAssetManager
) : FakeYouRemoteDataSource {

    override suspend fun getVoiceModels(): List<NetworkVoiceModel> = withContext(Dispatchers.IO) {
        val jsonAdapter: JsonAdapter<NetworkVoiceModels> = moshi.adapter(NetworkVoiceModels::class.java)
        jsonAdapter.fromJson(fakeAssetManager.open(VOICE_MODELS).source().buffer())!!.models
    }

    override suspend fun getCategories(): List<NetworkCategory> = withContext(Dispatchers.IO) {
        val jsonAdapter: JsonAdapter<NetworkCategories> = moshi.adapter(NetworkCategories::class.java)
        jsonAdapter.fromJson(fakeAssetManager.open(CATEGORIES).source().buffer())!!.categories
    }
    override suspend fun posTtsRequest(networkTtsRequestBody: NetworkTtsRequestBody): String = withContext(Dispatchers.IO) {
        val jsonAdapter: JsonAdapter<NetworkTtsRequestBody> = moshi.adapter(NetworkTtsRequestBody::class.java)
        val json = jsonAdapter.toJson(networkTtsRequestBody)
        fakeAssetManager.write(REQUESTED_TTS, json)
        NetworkTtsResponse(true, "JTINF:qsy72wnfashhvnkktc16y49cy1").inference_job_token
    }

    override suspend fun getTtsRequestState(inferenceToken: String): NetworkTtsRequestState = withContext(Dispatchers.IO) {
        val jsonAdapter: JsonAdapter<NetworkTtsRequestStatus> = moshi.adapter(NetworkTtsRequestStatus::class.java)
        jsonAdapter.fromJson(fakeAssetManager.open(REQUESTED_TTS_STATUS).source().buffer())!!.state
    }

    companion object {
        private const val VOICE_MODELS = "voiceModels.json"
        private const val CATEGORIES = "categories.json"
        private const val REQUESTED_TTS = "requestedTts.json"
        private const val REQUESTED_TTS_STATUS = "requestedTtsStatus.json"
    }
}