package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.common.enums.TtsRequestStatusType
import com.joseg.fakeyouclient.data.model.asTtsRequestStateCompact
import com.joseg.fakeyouclient.model.TtsRequestStateCompact
import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import com.joseg.fakeyouclient.network.model.NetworkTtsRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class TtsRequestRepository @Inject constructor(
    private val fakeRemoteDataSource: FakeYouRemoteDataSource
) {

    fun postTtsRequest(modelToken: String, inferenceText: String): Flow<String> = flow {
        emit(
            fakeRemoteDataSource.posTtsRequest(NetworkTtsRequestBody(
                tts_model_token = modelToken,
                uuid_idempotency_token = UUID.randomUUID().toString(),
                inference_text = inferenceText
            ))
        )
    }
        .flowOn(Dispatchers.IO)

    fun pollTtsRequestState(inferenceJobToken: String): Flow<TtsRequestStateCompact> = flow  {
        var flag = true
        while (flag) {
            val ttsRequestState = fakeRemoteDataSource.getTtsRequestState(inferenceJobToken)
            flag = when (TtsRequestStatusType.parse(ttsRequestState.status)) {
                TtsRequestStatusType.PENDING,
                TtsRequestStatusType.STARTED,
                TtsRequestStatusType.ATTEMPT_FAILED -> true
                else -> false
            }
            emit(ttsRequestState)
            delay(2000L)
        }
    }
        .map { it.asTtsRequestStateCompact() }
        .flowOn(Dispatchers.IO)
}