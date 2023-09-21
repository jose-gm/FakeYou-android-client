package com.joseg.fakeyouclient.data.fake.repository

import com.joseg.fakeyouclient.data.ApiResult
import com.joseg.fakeyouclient.data.repository.TtsRequestRepository
import com.joseg.fakeyouclient.data.testdouble.model.TtsRequestDummies
import com.joseg.fakeyouclient.model.TtsRequestState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTtsRequestRepository : TtsRequestRepository {
    val list = mutableListOf<Pair<String, String>>()
    override suspend fun postTtsRequest(
        modelToken: String,
        inferenceText: String
    ): ApiResult<String> {
        list.add(Pair(modelToken, inferenceText))
        return ApiResult.toApiResult { TtsRequestDummies.dummyInferenceJobToken }
    }

    override fun pollTtsRequestStateFlow(inferenceJobToken: String): Flow<TtsRequestState> = flow {
        emit(TtsRequestDummies.dummyPendingTtsRequestState)
        delay(1000L)
        repeat(4) {
            emit(TtsRequestDummies.dummyStartedTtsRequestState)
            delay(1000L)
        }
        delay(1000L)
        emit(TtsRequestDummies.dummyCompletedTtsRequestState)
    }
}