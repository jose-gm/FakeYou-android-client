package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.common.enums.TtsRequestStatusType
import com.joseg.fakeyouclient.data.ApiResult
import com.joseg.fakeyouclient.data.fake.datasource.FakeYouRemoteDataSource
import com.joseg.fakeyouclient.data.repository.implementation.BaseTtsRequestRepository
import com.joseg.fakeyouclient.model.TtsRequestState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class BaseTtsRequestRepositoryTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val unconfinedDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(unconfinedDispatcher)
    private lateinit var baseTtsRequestRepository: BaseTtsRequestRepository
    private lateinit var remoteDataSource: com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
    private val dummyInferenceJobTokenResponse = "JTINF:qsy72wnfashhvnkktc16y49cy1"

    @Before
    fun setUp() {
        remoteDataSource = FakeYouRemoteDataSource()
    }

    @Test
    fun `post tts request and receive inferenceJobToken`() = testScope.runTest {
        baseTtsRequestRepository = BaseTtsRequestRepository(remoteDataSource, unconfinedDispatcher)
        val response = baseTtsRequestRepository.postTtsRequest("TM:vjz1xt47gjay", "I'm Blitzcrank")

        val inferenceJobToken = (response as ApiResult.Success).data
        assertEquals(inferenceJobToken, dummyInferenceJobTokenResponse)
    }

    @Test
    fun pollTtsRequestState() = testScope.runTest {
        baseTtsRequestRepository = BaseTtsRequestRepository(remoteDataSource, unconfinedDispatcher)
        val stateValues = listOf(
                TtsRequestState(
                jobToken = "JTINF:qsy72wnfashhvnkktc16y49cy1",
                status = TtsRequestStatusType.parse("pending"),
                maybeExtraStatusDescription = null,
                attemptCount = 0,
                maybeResultToken = null,
                maybePublicBucketWavAudioPath = null,
                modelToken = "TM:7wbtjphx8h8v",
                ttsModelType = "tacotron2",
                title = "Mario*",
                rawInferenceText = "This is a use of the voice",
                createdAt = "2022-02-28T05:39:36Z",
                updatedAt = "2022-02-28T05:39:51Z"
            ),
            TtsRequestState(
                jobToken = "JTINF:qsy72wnfashhvnkktc16y49cy1",
                status = TtsRequestStatusType.parse("pending"),
                maybeExtraStatusDescription = null,
                attemptCount = 0,
                maybeResultToken = null,
                maybePublicBucketWavAudioPath = null,
                modelToken = "TM:7wbtjphx8h8v",
                ttsModelType = "tacotron2",
                title = "Mario*",
                rawInferenceText = "This is a use of the voice",
                createdAt = "2022-02-28T05:39:36Z",
                updatedAt = "2022-02-28T05:39:51Z"
            )
        )

        val job = launch {
            val data = baseTtsRequestRepository.pollTtsRequestStateFlow(dummyInferenceJobTokenResponse)
                .take(2)
                .toList()
            assertEquals(data.size, 2)
            assertEquals(data.first(), stateValues.first())
        }
        job.cancel()
    }
}