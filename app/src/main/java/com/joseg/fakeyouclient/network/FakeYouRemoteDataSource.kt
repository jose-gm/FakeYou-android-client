package com.joseg.fakeyouclient.network

import com.joseg.fakeyouclient.network.model.NetworkCategory
import com.joseg.fakeyouclient.network.model.NetworkTtsRequestBody
import com.joseg.fakeyouclient.network.model.NetworkTtsRequestState
import com.joseg.fakeyouclient.network.model.NetworkVoiceModel


interface FakeYouRemoteDataSource {

    suspend fun getVoiceModels(): List<NetworkVoiceModel>

    suspend fun getCategories(): List<NetworkCategory>

    suspend fun posTtsRequest(networkTtsRequestBody: NetworkTtsRequestBody): String

    suspend fun getTtsRequestState(inferenceToken: String): NetworkTtsRequestState
}