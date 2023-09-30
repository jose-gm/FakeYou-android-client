package com.joseg.fakeyouclient.ui.feature.audioList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joseg.fakeyouclient.data.download.DownloadManager
import com.joseg.fakeyouclient.data.download.DownloadState
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudiosViewModel @Inject constructor(
    private val getAudiosUseCase: GetAudiosUseCase,
    private val deleteAudioUseCase: DeleteAudioUseCase,
    private val downloadManager: DownloadManager,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _audiosFlow = getAudiosUseCase()
    private val _audioItemUiStateMapFlow = MutableStateFlow<Map<String, AudioItemUiState>>(emptyMap())

    val audiosUiStateFlow: StateFlow<UiState<AudiosUiState>> = combine(
        _audiosFlow,
        _audioItemUiStateMapFlow
    ) { audios, audioStateMap ->
        AudiosUiState(
            audios.map { audio ->
                audioStateMap[audio.id]?.copy()
                    ?: AudioItemUiState(
                        audio = audio,
                        isPlaying = false,
                        playbackPosition = 0L,
                        downloadProgress = null,
                        downloadState = null
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
        _audioItemUiStateMapFlow.update { map ->
            val newMap = map.toMutableMap()
            val previousPlayingAudioItemKey = newMap.keys.find {
                it != uiState.audio.id &&
                        (newMap[it]?.isPlaying == true && uiState.isPlaying)  }

            newMap[uiState.audio.id] = uiState.copy(downloadProgress = null)
            previousPlayingAudioItemKey?.let {
                newMap[it] = newMap[it]!!.copy(
                    isPlaying = false,
                    playbackPosition = previousPlaybackPosition ?: 0,
                    downloadProgress = null)
            }
            newMap
        }
    }

    fun isAudioDownloaded(audioItemUiState: AudioItemUiState): Boolean = downloadManager.isAudioDownloaded(audioItemUiState.audio)

    fun getAudioFilePath(audioItemUiState: AudioItemUiState): String? = downloadManager.getAudioFilePath(audioItemUiState.audio)

    fun startDownload(audioItemUiState: AudioItemUiState) = viewModelScope.launch(ioDispatcher) {
        val newMap = _audioItemUiStateMapFlow.value.toMutableMap()
        downloadManager.downloadAudio(audioItemUiState.audio)
        downloadManager.getDownloadProgress(audioItemUiState.audio).collect { progress ->
            newMap[audioItemUiState.audio.id] = audioItemUiState.copy(
                isPlaying = false,
                playbackPosition = 0,
                downloadProgress = progress,
                downloadState = DownloadState.DOWNLOADING)
            _audioItemUiStateMapFlow.emit(newMap)
        }

        newMap[audioItemUiState.audio.id] = audioItemUiState.copy(
            isPlaying = false,
            playbackPosition = 0,
            downloadProgress = null,
            downloadState = downloadManager.getDownloadState(audioItemUiState.audio))
        _audioItemUiStateMapFlow.emit(newMap)
    }

    data class AudiosUiState(
        val items: List<AudioItemUiState>
    )
    data class AudioItemUiState(
        val audio: Audio,
        val isPlaying: Boolean,
        val playbackPosition: Long,
        val downloadProgress: Int?,
        val downloadState: DownloadState?
    )
}