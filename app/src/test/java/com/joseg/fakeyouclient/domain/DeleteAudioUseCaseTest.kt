package com.joseg.fakeyouclient.domain

import com.joseg.fakeyouclient.data.fake.repository.FakeAudioRepository
import com.joseg.fakeyouclient.data.testdouble.model.AudioDummies
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DeleteAudioUseCaseTest {
    private lateinit var fakeAudioRepository: FakeAudioRepository
    private lateinit var deleteAudioUseCase: DeleteAudioUseCase

    @Before
    fun setUp() {
        fakeAudioRepository = FakeAudioRepository()
        deleteAudioUseCase = DeleteAudioUseCase(fakeAudioRepository)
    }

    @Test
    fun delete() = runTest(UnconfinedTestDispatcher()) {
        fakeAudioRepository.insert(AudioDummies.dummy1)
        fakeAudioRepository.insert(AudioDummies.dummy2)

        val audios = fakeAudioRepository.getAll()
        assertEquals(audios.first()[0], AudioDummies.dummy1)
        assertEquals(audios.first()[1], AudioDummies.dummy2)

        deleteAudioUseCase.delete(listOf(audios.first()[0]))
        assertEquals(audios.first()[0], AudioDummies.dummy2)
    }

    @Test
    fun deleteAll() = runTest {
        fakeAudioRepository.insert(AudioDummies.dummy3)
        fakeAudioRepository.insert(AudioDummies.dummy4)

        assertEquals(fakeAudioRepository.getAll().first().isNotEmpty(), true)

        deleteAudioUseCase.deleteAll()

        assertEquals(fakeAudioRepository.getAll().first().isEmpty(), true)
    }
}