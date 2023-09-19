package com.joseg.fakeyouclient.data.repository.implementation

import com.joseg.fakeyouclient.common.enums.TtsRequestStatusType
import com.joseg.fakeyouclient.data.ApiResult
import com.joseg.fakeyouclient.data.model.asTtsRequestState
import com.joseg.fakeyouclient.data.repository.TtsRequestRepository
import com.joseg.fakeyouclient.model.TtsRequestState
import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import com.joseg.fakeyouclient.network.model.NetworkTtsRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class BaseTtsRequestRepository @Inject constructor(
    private val fakeRemoteDataSource: FakeYouRemoteDataSource
) : TtsRequestRepository {
    override suspend fun postTtsRequest(modelToken: String, inferenceText: String): ApiResult<String> = withContext(Dispatchers.IO) {
        ApiResult.toApiResult {
            fakeRemoteDataSource.posTtsRequest(
                NetworkTtsRequestBody(
                    tts_model_token = modelToken,
                    uuid_idempotency_token = UUID.randomUUID().toString(),
                    inference_text = inferenceText
                )
            )
        }
    }

    override fun pollTtsRequestStateFlow(inferenceJobToken: String): Flow<TtsRequestState> = flow  {
        while (true) {
            val ttsRequestState = fakeRemoteDataSource.getTtsRequestState(inferenceJobToken)
            when (TtsRequestStatusType.parse(ttsRequestState.status)) {
                TtsRequestStatusType.PENDING,
                TtsRequestStatusType.STARTED,
                TtsRequestStatusType.ATTEMPT_FAILED -> true

                TtsRequestStatusType.DEAD,
                TtsRequestStatusType.COMPLETE_FAILURE,
                TtsRequestStatusType.COMPLETE_SUCCESS-> {
                    emit(ttsRequestState)
                    break
                }
            }
            emit(ttsRequestState)
            delay(2000L)
        }
    }
        .map { it.asTtsRequestState() }
        .flowOn(Dispatchers.IO)
        .distinctUntilChanged()
}