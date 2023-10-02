package com.joseg.fakeyouclient.domain

import com.joseg.fakeyouclient.common.enums.TtsRequestStatusType
import com.joseg.fakeyouclient.data.fake.repository.FakeTtsRequestRepository
import com.joseg.fakeyouclient.data.testdouble.model.TtsRequestDummies
import com.joseg.fakeyouclient.model.TtsRequestState
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PollTtsRequestUseCaseTest {
    private lateinit var pollTtsRequestStateUseCase: PollTtsRequestStateUseCase

    @Before
    fun setUp() {
        pollTtsRequestStateUseCase = PollTtsRequestStateUseCase(FakeTtsRequestRepository())
    }

    @Test
    fun pollTtsRequestState() = runTest {
        val states = mutableListOf<TtsRequestState>()
        pollTtsRequestStateUseCase(TtsRequestDummies.dummyInferenceJobToken).toList(states)

        advanceTimeBy(1100L)
        assertEquals(states[0].status, TtsRequestStatusType.PENDING)

        advanceTimeBy(1100L)
        assertEquals(states[1].status, TtsRequestStatusType.STARTED)

        advanceTimeBy(1100L)
        assertEquals(states[2].status, TtsRequestStatusType.STARTED)

        advanceTimeBy(1100L)
        assertEquals(states[3].status, TtsRequestStatusType.STARTED)

        advanceTimeBy(1100L)
        assertEquals(states[4].status, TtsRequestStatusType.STARTED)

        advanceTimeBy(1100L)
        assertEquals(states[5].status, TtsRequestStatusType.COMPLETE_SUCCESS)
    }
}