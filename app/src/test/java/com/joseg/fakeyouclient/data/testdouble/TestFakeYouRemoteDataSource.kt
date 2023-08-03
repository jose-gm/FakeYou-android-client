package com.joseg.fakeyouclient.data.testdouble

import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import com.joseg.fakeyouclient.data.fake.FakeAssetManager
import com.joseg.fakeyouclient.data.fake.FakeRemoteDataSource
import com.joseg.fakeyouclient.network.model.NetworkCategory
import com.joseg.fakeyouclient.network.model.NetworkTtsRequestBody
import com.joseg.fakeyouclient.network.model.NetworkTtsRequestState
import com.joseg.fakeyouclient.network.model.NetworkTtsResponse
import com.joseg.fakeyouclient.network.model.NetworkVoiceModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking

class TestFakeYouRemoteDataSource : FakeYouRemoteDataSource {

    private val source = FakeRemoteDataSource(
        Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build(),
        FakeAssetManager
    )

    override suspend fun getVoiceModels(): List<NetworkVoiceModel> = runBlocking { source.getVoiceModels() }

    override suspend fun getCategories(): List<NetworkCategory> = runBlocking { source.getCategories() }

    override suspend fun posTtsRequest(networkTtsRequestBody: NetworkTtsRequestBody): String = source.posTtsRequest(networkTtsRequestBody)

    override suspend fun getTtsRequestState(inferenceToken: String): NetworkTtsRequestState = source.getTtsRequestState(inferenceToken)
}