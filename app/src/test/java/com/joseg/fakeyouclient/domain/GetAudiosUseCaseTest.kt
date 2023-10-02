package com.joseg.fakeyouclient.domain
import com.joseg.fakeyouclient.data.fake.repository.FakeAudioRepository
import com.joseg.fakeyouclient.data.testdouble.model.AudioDummies
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class GetAudiosUseCaseTest {

    @Test
    fun getAudios() = runTest {
        val fakeAudioRepository = FakeAudioRepository()
        val getAudiosUseCase = GetAudiosUseCase(fakeAudioRepository)

        fakeAudioRepository.insert(AudioDummies.dummy1)
        fakeAudioRepository.insert(AudioDummies.dummy2)

        val audios = getAudiosUseCase()
        assertEquals(audios.first()[0], AudioDummies.dummy1)
        assertEquals(audios.first()[1], AudioDummies.dummy2)
    }
}