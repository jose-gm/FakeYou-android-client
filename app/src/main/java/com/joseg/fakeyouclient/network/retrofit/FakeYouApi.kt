package com.joseg.fakeyouclient.network.retrofit

import com.joseg.fakeyouclient.network.model.NetworkCategories
import com.joseg.fakeyouclient.network.model.NetworkTtsRequestBody
import com.joseg.fakeyouclient.network.model.NetworkTtsRequestStatus
import com.joseg.fakeyouclient.network.model.NetworkTtsResponse
import com.joseg.fakeyouclient.network.model.NetworkVoiceModels
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FakeYouApi {
    @GET("tts/list")
    suspend fun getVoiceModels(): NetworkVoiceModels

    @GET("category/list/tts")
    suspend fun getCategories(): NetworkCategories

    @POST("tts/inference")
    suspend fun posTtsRequest(@Body networkTtsRequestBody: NetworkTtsRequestBody): NetworkTtsResponse

    @GET("tts/job/{inferenceJobToken}")
    suspend fun getTtsRequestStatus(@Path("inferenceJobToken") token: String): NetworkTtsRequestStatus
}