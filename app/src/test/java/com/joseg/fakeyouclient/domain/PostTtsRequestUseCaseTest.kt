package com.joseg.fakeyouclient.domain

import com.joseg.fakeyouclient.data.ApiResult
import com.joseg.fakeyouclient.data.fake.repository.FakeTtsRequestRepository
import com.joseg.fakeyouclient.data.testdouble.model.TtsRequestDummies
import com.joseg.fakeyouclient.data.testdouble.model.VoiceModelDummies
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class PostTtsRequestUseCaseTest {
    private lateinit var postTtsRequestUseCase: PostTtsRequestUseCase

    @Before
    fun setUp() {
        postTtsRequestUseCase = PostTtsRequestUseCase(FakeTtsRequestRepository())
    }

    @Test
    fun postTtsRequest() = runTest {
        val response = postTtsRequestUseCase(VoiceModelDummies.dummyModelToken, VoiceModelDummies.dummyIneferenceText)
        val inferenceJobToken = (response as ApiResult.Success).data
        assertEquals(inferenceJobToken, TtsRequestDummies.dummyInferenceJobToken)
    }
}