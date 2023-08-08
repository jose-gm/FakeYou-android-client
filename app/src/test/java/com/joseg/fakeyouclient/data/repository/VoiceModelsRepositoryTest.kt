package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.data.testdouble.TestFakeYouRemoteDataSource
import com.joseg.fakeyouclient.model.VoiceModelCompact
import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class VoiceModelsRepositoryTest {

    private lateinit var voiceModelsRepository: VoiceModelsRepository
    private lateinit var remoteDataSource: FakeYouRemoteDataSource

    @Before
    fun setUp() {
        remoteDataSource = TestFakeYouRemoteDataSource()
        voiceModelsRepository = VoiceModelsRepository(remoteDataSource)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `successfully get voiceModels from remoteSource`() = runTest(UnconfinedTestDispatcher()) {
        val voiceModelsFlow = voiceModelsRepository.getVoiceModels()
        val dummyVoiceModelCompact = VoiceModelCompact(
            modelToken = "TM:bysebgf36tkg",
            ttsModelType = "tacotron2",
            creatorDisplayName = "zombie",
            title = "Arthur C. Clarke (901ep)",
            ietfPrimaryLanguageSubtag = "en",
            categoryTokens = listOf(
                "CAT:46m8yaq2ceg",
                "CAT:gty64wem67f",
                "CAT:jhskand3g24"
            )
        )

        assertEquals(voiceModelsFlow.first().first(), dummyVoiceModelCompact)
    }
}