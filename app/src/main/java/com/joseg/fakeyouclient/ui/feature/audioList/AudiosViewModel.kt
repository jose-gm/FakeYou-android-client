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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AudiosViewModel @Inject constructor(
    private val getAudiosUseCase: GetAudiosUseCase,
    private val deleteAudioUseCase: DeleteAudioUseCase,
    private val downloadManager: DownloadManager,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _audiosFlow = getAudiosUseCase()
    private val _searchQueryStateFlow = MutableStateFlow("")
    private val _audioStateMapFlow = MutableStateFlow<Map<String, AudioItemUiState>>(emptyMap())
    private val _selectedAudioUiItemMapFlow = MutableStateFlow<List<AudioItemUiState>>(emptyList())
    private val _multiSelectActionBlockFlow = MutableStateFlow(false)
    val isMultiSelectActionOnFlow: Flow<Boolean> = _selectedAudioUiItemMapFlow
        .map { it.isNotEmpty() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    val audiosUiStateFlow: StateFlow<UiState<AudiosUiState>> = combine(
        _audiosFlow,
        _audioStateMapFlow,
        _selectedAudioUiItemMapFlow,
        _searchQueryStateFlow,
        _multiSelectActionBlockFlow
    ) { audios, audioStateMap, selectedAudioItems, searchQuery, isMultiSelectActionBlocked ->
        AudiosUiState(
            audios.map { audio ->
                AudioItemUiState(
                    audio = audio,
                    isPlaying = audioStateMap[audio.id]?.isPlaying ?: false,
                    playbackPosition = audioStateMap[audio.id]?.playbackPosition ?: 0L,
                    downloadProgress = audioStateMap[audio.id]?.downloadProgress ?: 0,
                    downloadState = audioStateMap[audio.id]?.downloadState,
                    isSelected = selectedAudioItems.any { it.audio.id == audio.id },
                    isMultiSelectActionOn = selectedAudioItems.isNotEmpty(),
                    isMultiSelectActionBlocked = isMultiSelectActionBlocked
                )
            }
                .filter { it.audio.inferenceText.lowercase().contains(searchQuery) ||
                        it.audio.voiceModelName.lowercase().contains(searchQuery)}
                .sortedByDescending { it.audio.createdAt }
        )
    }
        .asUiStateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = UiState.Loading
        )

    fun updateAudioState(uiState: AudioItemUiState, previousPlaybackPosition: Long? = null) {
        _audioStateMapFlow.update { map ->
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
        val newMap = _audioStateMapFlow.value.toMutableMap()
        downloadManager.downloadAudio(audioItemUiState.audio)
        downloadManager.getDownloadProgress(audioItemUiState.audio).collect { progress ->
            newMap[audioItemUiState.audio.id] = audioItemUiState.copy(
                isPlaying = false,
                playbackPosition = 0,
                downloadProgress = progress,
                downloadState = DownloadState.DOWNLOADING)
            _audioStateMapFlow.emit(newMap)
        }

        newMap[audioItemUiState.audio.id] = audioItemUiState.copy(
            isPlaying = false,
            playbackPosition = 0,
            downloadProgress = null,
            downloadState = downloadManager.getDownloadState(audioItemUiState.audio))
        _audioStateMapFlow.emit(newMap)
    }

    fun selectedAudioItem(audioItemUiState: AudioItemUiState) {
        _selectedAudioUiItemMapFlow.update { list ->
            val newList = list.toMutableList()
            newList.find { it.audio.id == audioItemUiState.audio.id }?.let { item ->
                newList.remove(item)
            } ?: newList.add(audioItemUiState)
            newList
        }
    }

    fun deactivateMultiSelectAction() {
        _selectedAudioUiItemMapFlow.update {
            emptyList()
        }
    }

    fun blockMultiSelectAction() {
        _multiSelectActionBlockFlow.value = true
    }

    fun unblockMultiSelectAction() {
        _multiSelectActionBlockFlow.value = false
    }

    fun deleteSelectedAudios() = viewModelScope.launch {
        withContext(ioDispatcher) {
            _selectedAudioUiItemMapFlow.value.forEach {
                downloadManager.deleteAudioFile(it.audio)
            }
        }
        deleteAudioUseCase.delete(_selectedAudioUiItemMapFlow.value.map { it.audio })
        deactivateMultiSelectAction()
    }

    fun submitSearchQuery(text: String) {
        _searchQueryStateFlow.value = text.lowercase()
    }

    data class AudiosUiState(
        val items: List<AudioItemUiState>
    )

    data class AudioItemUiState(
        val audio: Audio,
        val isPlaying: Boolean,
        val playbackPosition: Long,
        val downloadProgress: Int?,
        val downloadState: DownloadState?,
        val isSelected: Boolean,
        val isMultiSelectActionOn: Boolean,
        val isMultiSelectActionBlocked: Boolean
    )
}