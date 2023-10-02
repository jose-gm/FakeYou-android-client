package com.joseg.fakeyouclient.domain

import com.joseg.fakeyouclient.common.enums.LanguageTag
import com.joseg.fakeyouclient.data.fake.repository.FakeVoiceModelRepository
import com.joseg.fakeyouclient.data.testdouble.model.VoiceModelDummies
import com.joseg.fakeyouclient.model.UserRatings
import com.joseg.fakeyouclient.model.VoiceModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SaveVoiceModelUseCaseTest {
    private lateinit var saveVoiceModelUseCase: SaveVoiceModelUseCase
    private lateinit var getVoiceModelUseCase: GetVoiceModelUseCase

    @Before
    fun setUp() {
        val voiceModelRepository = FakeVoiceModelRepository()
        saveVoiceModelUseCase = SaveVoiceModelUseCase(voiceModelRepository)
        getVoiceModelUseCase = GetVoiceModelUseCase(voiceModelRepository)
    }

    @Test
    fun saveVoiceModel() = runTest {
        saveVoiceModelUseCase(VoiceModelDummies.dummy1)
        assertEquals(getVoiceModelUseCase.getVoiceModel(), VoiceModelDummies.dummy1)
    }
}