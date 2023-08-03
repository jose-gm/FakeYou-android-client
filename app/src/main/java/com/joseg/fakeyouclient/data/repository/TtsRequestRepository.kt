package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.data.model.asTtsRequestStateCompact
import com.joseg.fakeyouclient.model.TtsRequestStateCompact
import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import com.joseg.fakeyouclient.network.model.NetworkTtsRequestBody
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class TtsRequestRepository @Inject constructor(
    private val fakeRemoteDataSource: FakeYouRemoteDataSource,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
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
        .flowOn(coroutineDispatcher)

    fun pollTtsRequestState(inferenceJobToken: String, predicate: () -> Boolean = { true }): Flow<TtsRequestStateCompact> = flow  {
        while (predicate()) {
            emit(fakeRemoteDataSource.getTtsRequestState(inferenceJobToken))
            delay(2000L)
        }
    }
        .map { it.asTtsRequestStateCompact() }
        .flowOn(coroutineDispatcher)
}