package com.joseg.fakeyouclient.domain

import com.joseg.fakeyouclient.data.ApiResult
import com.joseg.fakeyouclient.data.repository.TtsRequestRepository
import javax.inject.Inject

class PostTtsRequestUseCase @Inject constructor(
    private val ttsRequestRepository: TtsRequestRepository
) {
    suspend operator fun invoke(modelToken: String, inferenceText: String): ApiResult<String> =
        ttsRequestRepository.postTtsRequest(modelToken, inferenceText)
}