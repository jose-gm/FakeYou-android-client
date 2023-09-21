package com.joseg.fakeyouclient.domain

import com.joseg.fakeyouclient.data.ApiResult
import com.joseg.fakeyouclient.data.fake.repository.FakeVoiceModelRepository
import com.joseg.fakeyouclient.data.testdouble.model.VoiceModelDummies
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetVoiceModelUseCaseTest {
    private lateinit var getVoiceModelUseCase: GetVoiceModelUseCase

    @Before
    fun setUp() {
        getVoiceModelUseCase = GetVoiceModelUseCase(FakeVoiceModelRepository())
    }

    @Test
    fun getVoiceModels() = runTest {
        val voiceModels = (getVoiceModelUseCase.getVoiceModels().first() as ApiResult.Success).data
        assertEquals(voiceModels.first(), VoiceModelDummies.dummyList.first())
    }

    @Test
    fun getSavedVoiceModel() = runTest {
        assertEquals(getVoiceModelUseCase.getVoiceModel(), VoiceModelDummies.dummy2)
    }
}