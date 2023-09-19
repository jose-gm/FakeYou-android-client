package com.joseg.fakeyouclient.domain

import com.joseg.fakeyouclient.data.repository.TtsRequestRepository
import com.joseg.fakeyouclient.model.TtsRequestState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PollTtsRequestStateUseCase @Inject constructor(
    private val ttsRequestRepository: TtsRequestRepository
) {
    operator fun invoke(inferenceJobToken: String): Flow<TtsRequestState> =
        ttsRequestRepository.pollTtsRequestStateFlow(inferenceJobToken)
}