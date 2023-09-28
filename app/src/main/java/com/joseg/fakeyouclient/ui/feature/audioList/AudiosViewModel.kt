package com.joseg.fakeyouclient.ui.feature.audioList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joseg.fakeyouclient.data.download.DownloadManager
import com.joseg.fakeyouclient.domain.DeleteAudioUseCase
import com.joseg.fakeyouclient.domain.GetAudiosUseCase
import com.joseg.fakeyouclient.model.Audio
import com.joseg.fakeyouclient.ui.shared.UiState
import com.joseg.fakeyouclient.ui.shared.asUiStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AudiosViewModel @Inject constructor(
    private val getAudiosUseCase: GetAudiosUseCase,
    private val deleteAudioUseCase: DeleteAudioUseCase,
    private val downloadManager: DownloadManager,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _audiosFlow = getAudiosUseCase()
    private val _audioItemUiStateListFlow = MutableStateFlow<List<AudioItemUiState>>(emptyList())

    val audiosUiStateFlow: StateFlow<UiState<AudiosUiState>> = combine(
        _audiosFlow,
        _audioItemUiStateListFlow
    ) { audios, audioStateList ->
        AudiosUiState(
            audios.map { audio ->
                audioStateList.find { it.audio.id == audio.id }?.copy()
                    ?: AudioItemUiState(
                        audio = audio,
                        isPlaying = false,
                        playbackPosition = 0L,
                    )
            }.sortedByDescending { it.audio.createdAt }
        )
    }
        .asUiStateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = UiState.Loading
        )

    fun updateAudioUiItemState(uiState: AudioItemUiState, previousPlaybackPosition: Long? = null) {
        _audioItemUiStateListFlow.update { list ->
            val newList = list.toMutableList()
            if (newList.find { it.audio.id == uiState.audio.id } == null) {
                newList.add(uiState)
            }

            newList.map { audioItemUiState ->
                when {
                    audioItemUiState.audio.id == uiState.audio.id -> {
                        audioItemUiState.copy(
                            audio = uiState.audio,
                            isPlaying = uiState.isPlaying,
                            playbackPosition = uiState.playbackPosition
                        )
                    }
                    (audioItemUiState.audio.id != uiState.audio.id) &&
                            (audioItemUiState.isPlaying && uiState.isPlaying) -> {
                        audioItemUiState.copy(isPlaying = false, playbackPosition = previousPlaybackPosition ?: 0)
                    }
                    else -> audioItemUiState.copy(isPlaying = false)
                }
            }
        }
    }

    fun isAudioDownloaded(audioItemUiState: AudioItemUiState): Boolean = downloadManager.isAudioDownloaded(audioItemUiState.audio)

    fun getAudioFilePath(audioItemUiState: AudioItemUiState): String? = downloadManager.getAudioFilePath(audioItemUiState.audio)

    data class AudiosUiState(
        val items: List<AudioItemUiState>
    )
    data class AudioItemUiState(
        val audio: Audio,
        val isPlaying: Boolean,
        val playbackPosition: Long,
    )
}