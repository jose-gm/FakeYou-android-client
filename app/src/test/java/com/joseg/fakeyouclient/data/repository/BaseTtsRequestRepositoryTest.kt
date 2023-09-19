package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.data.repository.implementation.BaseTtsRequestRepository
import com.joseg.fakeyouclient.data.testdouble.TestFakeYouRemoteDataSource
import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class BaseTtsRequestRepositoryTest {

    private lateinit var baseTtsRequestRepository: BaseTtsRequestRepository
    private lateinit var remoteDataSource: FakeYouRemoteDataSource

    @Before
    fun setUp() {
        remoteDataSource = TestFakeYouRemoteDataSource()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `postTtsRequest successfully post tts request and return inference token`() = runTest(UnconfinedTestDispatcher()) {
        baseTtsRequestRepository = BaseTtsRequestRepository(remoteDataSource)
        val responseFlow = baseTtsRequestRepository.postTtsRequest("TM:vjz1xt47gjay", "I'm Blitzcrank")
        val dummyResponse = "JTINF:qsy72wnfashhvnkktc16y49cy1"

        assertEquals(responseFlow.single(), dummyResponse)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun pollTtsRequestState() = runTest {
        baseTtsRequestRepository = BaseTtsRequestRepository(remoteDataSource)
        val pollFlow = baseTtsRequestRepository.pollTtsRequestStateFlow("JTINF:qsy72wnfashhvnkktc16y49cy1")

        launch {
            val data = pollFlow.take(2).toList()
            assertEquals(data.size, 2)
        }

        advanceTimeBy(5000L)
    }
}