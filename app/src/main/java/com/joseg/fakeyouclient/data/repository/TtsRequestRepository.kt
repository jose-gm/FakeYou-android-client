package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.data.ApiResult
import com.joseg.fakeyouclient.model.TtsRequestState
import kotlinx.coroutines.flow.Flow

interface TtsRequestRepository {
    suspend fun postTtsRequest(modelToken: String, inferenceText: String): ApiResult<String>
    fun pollTtsRequestStateFlow(inferenceJobToken: String): Flow<TtsRequestState>
}