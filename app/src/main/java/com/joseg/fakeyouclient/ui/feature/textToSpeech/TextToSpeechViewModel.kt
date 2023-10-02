package com.joseg.fakeyouclient.ui.feature.textToSpeech

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joseg.fakeyouclient.ui.shared.UiState
import com.joseg.fakeyouclient.ui.shared.asUiState
import com.joseg.fakeyouclient.domain.GetVoiceModelUseCase
import com.joseg.fakeyouclient.domain.PostTtsRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TextToSpeechViewModel @Inject constructor(
    private val getVoiceModelUseCase: GetVoiceModelUseCase,
    private val postTtsRequestUseCase: PostTtsRequestUseCase
) : ViewModel() {

    private val _submittedTtsRequestFlow = MutableSharedFlow<UiState<String>>()
    val submittedTtsRequestFlow = _submittedTtsRequestFlow.asSharedFlow()

    fun getVoiceModel() = getVoiceModelUseCase.getVoiceModel()

    fun postTtsRequest(voiceModelId: String, inferenceText: String) = viewModelScope.launch {
        _submittedTtsRequestFlow.emit(postTtsRequestUseCase(voiceModelId, inferenceText).asUiState())
    }
}