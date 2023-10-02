package com.joseg.fakeyouclient.domain

import com.joseg.fakeyouclient.data.fake.repository.FakeAudioRepository
import com.joseg.fakeyouclient.data.testdouble.model.AudioDummies
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class InsertAudioUseCaseTest {

    @Test
    fun insert() = runTest(UnconfinedTestDispatcher()) {
        val fakeAudioRepository = FakeAudioRepository()
        val insertAudioUseCase = InsertAudioUseCase(fakeAudioRepository)
        insertAudioUseCase(AudioDummies.dummy2)

        assertEquals(AudioDummies.dummy2, fakeAudioRepository.getAll().first().first())
    }
}