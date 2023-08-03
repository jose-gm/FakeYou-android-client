package com.joseg.fakeyouclient.network.retrofit

import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import com.joseg.fakeyouclient.network.model.NetworkCategory
import com.joseg.fakeyouclient.network.model.NetworkTtsRequestBody
import com.joseg.fakeyouclient.network.model.NetworkTtsRequestState
import com.joseg.fakeyouclient.network.model.NetworkTtsResponse
import com.joseg.fakeyouclient.network.model.NetworkVoiceModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitRemoteDataSource @Inject constructor(
    private val fakeYouApi: FakeYouApi
) : FakeYouRemoteDataSource {

    override suspend fun getVoiceModels(): List<NetworkVoiceModel> =
        fakeYouApi.getVoiceModels().models

    override suspend fun getCategories(): List<NetworkCategory> =
        fakeYouApi.getCategories().categories

    override suspend fun posTtsRequest(networkTtsRequestBody: NetworkTtsRequestBody): String =
        fakeYouApi.posTtsRequest(networkTtsRequestBody).inference_job_token

    override suspend fun getTtsRequestState(inferenceToken: String): NetworkTtsRequestState =
        fakeYouApi.getTtsRequestStatus(inferenceToken).state
}