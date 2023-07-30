package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.common.Result
import com.joseg.fakeyouclient.common.asResult
import com.joseg.fakeyouclient.common.enums.TtsRequestStatusType
import com.joseg.fakeyouclient.common.mapResult
import com.joseg.fakeyouclient.data.model.asTtsRequestStateCompact
import com.joseg.fakeyouclient.model.TtsRequestStateCompact
import com.joseg.fakeyouclient.network.model.NetworkTtsRequestBody
import com.joseg.fakeyouclient.network.service.FakeYouApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.UUID
import javax.inject.Inject

class TtsRequestRepository @Inject constructor(private val fakeYouApi: FakeYouApi) {

    fun postTtsRequest(modelToken: String, inferenceText: String): Flow<Result<String>> = flow {
        emit(
            fakeYouApi.posTtsRequest(NetworkTtsRequestBody(
                tts_model_token = modelToken,
                uuid_idempotency_token = UUID.randomUUID().toString(),
                inference_text = inferenceText
            ))
        )
    }
        .asResult()
        .mapResult { it.inference_job_token }
        .flowOn(Dispatchers.IO)

    fun pollTtsRequestState(inferenceJobToken: String, predicate: () -> Boolean = { true }): Flow<Result<TtsRequestStateCompact>> = flow {
        while (predicate()) {
            emit(fakeYouApi.getTtsRequestStatus(inferenceJobToken))
            delay(2000L)
        }
    }
        .asResult()
        .mapResult { it.asTtsRequestStateCompact() }
        .flowOn(Dispatchers.IO)
}