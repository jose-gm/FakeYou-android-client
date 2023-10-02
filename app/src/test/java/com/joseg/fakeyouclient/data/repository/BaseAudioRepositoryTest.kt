package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.data.fake.datasource.FakeAudioDatabaseSource
import com.joseg.fakeyouclient.data.repository.implementation.BaseAudioRepository
import com.joseg.fakeyouclient.data.testdouble.model.AudioDummies
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Assert.*
import org.junit.Test

class BaseAudioRepositoryTest {
    private lateinit var baseAudioRepository: BaseAudioRepository
    private val unconfinedDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        baseAudioRepository = BaseAudioRepository(FakeAudioDatabaseSource(), unconfinedDispatcher)
    }

    @Test
    fun insert() = runTest(unconfinedDispatcher) {
        baseAudioRepository.insert(AudioDummies.dummy1)
        assertEquals(baseAudioRepository.getAll().first().contains(AudioDummies.dummy1), true)
    }

    @Test
    fun delete() = runTest {
        baseAudioRepository.deleteAll()
        baseAudioRepository.insert(AudioDummies.dummy1)
        baseAudioRepository.delete(listOf(AudioDummies.dummy1))

        assertEquals(baseAudioRepository.getAll().first().isEmpty(), true)
    }

    @Test
    fun deleteAll() = runTest {
        baseAudioRepository.insert(AudioDummies.dummy1)
        baseAudioRepository.insert(AudioDummies.dummy2)
        baseAudioRepository.deleteAll()

        assertEquals(baseAudioRepository.getAll().first().isEmpty(), true)
    }

    @Test
    fun getAll() = runTest {
        baseAudioRepository.deleteAll()
        baseAudioRepository.insert(AudioDummies.dummy1)
        baseAudioRepository.insert(AudioDummies.dummy2)

        assertEquals(baseAudioRepository.getAll().first().size == 2, true)
    }
}