package com.joseg.fakeyouclient.ui.feature.textToSpeech

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joseg.fakeyouclient.ui.shared.UiState
import com.joseg.fakeyouclient.ui.shared.asUiState
import com.joseg.fakeyouclient.data.repository.TtsRequestRepository
import com.joseg.fakeyouclient.data.repository.VoiceSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TextToSpeechViewModel @Inject constructor(
    private val voiceSettingsRepository: VoiceSettingsRepository,
    private val ttsRequestRepository: TtsRequestRepository
) : ViewModel() {

    private val _submittedTtsRequestFlow = MutableSharedFlow<UiState<String>>()
    val submittedTtsRequestFlow = _submittedTtsRequestFlow.asSharedFlow()

    fun getVoiceModelDataSync() = voiceSettingsRepository.getVoiceModelSync()

    fun postTtsRequest(voiceModelId: String, inferenceText: String) = viewModelScope.launch {
        _submittedTtsRequestFlow.emit(ttsRequestRepository.postTtsRequest(voiceModelId, inferenceText).asUiState())
    }
}