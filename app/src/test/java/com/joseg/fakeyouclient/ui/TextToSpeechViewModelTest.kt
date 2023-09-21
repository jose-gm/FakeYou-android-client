package com.joseg.fakeyouclient.ui

import com.joseg.fakeyouclient.data.fake.repository.FakeTtsRequestRepository
import com.joseg.fakeyouclient.data.fake.repository.FakeVoiceModelRepository
import com.joseg.fakeyouclient.data.testdouble.model.TtsRequestDummies
import com.joseg.fakeyouclient.data.testdouble.model.VoiceModelDummies
import com.joseg.fakeyouclient.domain.GetVoiceModelUseCase
import com.joseg.fakeyouclient.domain.PostTtsRequestUseCase
import com.joseg.fakeyouclient.rule.MainDispatcherRule
import com.joseg.fakeyouclient.ui.feature.textToSpeech.TextToSpeechViewModel
import com.joseg.fakeyouclient.ui.shared.UiState
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class TextToSpeechViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var viewModel: TextToSpeechViewModel

    @Before
    fun setUp() {
        viewModel = TextToSpeechViewModel(
            GetVoiceModelUseCase(FakeVoiceModelRepository()),
            PostTtsRequestUseCase(FakeTtsRequestRepository())
        )
    }

    @Test
    fun getVoiceModel() = runTest {
        assertEquals(viewModel.getVoiceModel(), VoiceModelDummies.dummy2)
    }

    @Test
    fun postTtsRequest() = runTest {
        var states = mutableListOf<UiState<String>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.submittedTtsRequestFlow.toList(states)
        }

        viewModel.postTtsRequest(VoiceModelDummies.dummyModelToken, VoiceModelDummies.dummyIneferenceText)

        val inferenceJobToken = (states.first() as UiState.Success).data
        assertEquals(inferenceJobToken, TtsRequestDummies.dummyInferenceJobToken)
    }
}