package com.joseg.fakeyouclient.ui.feature.audioList.epoxy

import android.util.Log
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.airbnb.epoxy.TypedEpoxyController
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.ui.component.epoxymodels.EmptyScreenEpoxyModel
import com.joseg.fakeyouclient.ui.component.epoxymodels.LoadingScreenEpoxyModel
import com.joseg.fakeyouclient.ui.feature.audioList.AudiosViewModel
import com.joseg.fakeyouclient.ui.shared.UiState
import com.joseg.fakeyouclient.ui.shared.UiText
import com.joseg.fakeyouclient.ui.shared.onError
import com.joseg.fakeyouclient.ui.shared.onLoading
import com.joseg.fakeyouclient.ui.shared.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudiosEpoxyController(
    private val updateAudioUiItemState: (AudiosViewModel.AudioItemUiState, Long?) -> Unit,
    private val scope: CoroutineScope,
    private val isAudioDownloaded: (AudiosViewModel.AudioItemUiState) -> Boolean,
    private val getAudioFilePath: (AudiosViewModel.AudioItemUiState) -> String?,
    private val startDownload: (AudiosViewModel.AudioItemUiState) -> Unit
) : TypedEpoxyController<UiState<AudiosViewModel.AudiosUiState>>() {
    private lateinit var exoPlayer: ExoPlayer
    private var cancelFlow: Boolean = false

    override fun buildModels(data: UiState<AudiosViewModel.AudiosUiState>?) {
        data?.let { uiState ->
            uiState
                .onSuccess { audiosUIState ->
                    if (audiosUIState.items.isNotEmpty()) {
                        audiosUIState.items.forEach {
                            AudioItemEpoxyModel(
                                it,
                                updateAudioUiItemState,
                                isAudioDownloaded,
                                getAudioFilePath,
                                startDownload
                            )
                                .attachPlayer(exoPlayer)
                                .id(it.audio.id)
                                .addTo(this)
                        }
                    } else {
                        EmptyScreenEpoxyModel(
                            title = UiText.TextResource(R.string.no_audio_here_yet),
                            message = UiText.TextResource(R.string.you_will_see_your_generated_audios_here)
                        )
                            .id("empty_model")
                            .addTo(this)
                    }
                }
                .onLoading {
                    LoadingScreenEpoxyModel()
                        .id("loading-model")
                        .addTo(this)
                }
                .onError { throwable, i ->
                    Log.e("database", throwable.stackTraceToString())
                }
        }
    }

    fun setPlayer(player: ExoPlayer) {
        exoPlayer = player

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_ENDED -> {
                        val uiState = currentData
                        if (uiState is UiState.Success) {
                            val audiosUiState = uiState.data
                            audiosUiState.items.find {
                                it.audio.id == exoPlayer.currentMediaItem?.mediaId
                            }?.let {
                                updateAudioUiItemState(it.copy(isPlaying = false, playbackPosition = 0), null)
                            }
                        }
                    }
                }
            }
        })

        scope.launch {
            player.checkPlaybackPositionFlow().collect { progress ->
                val uiState = currentData
                if (uiState is UiState.Success) {
                    val audiosUiState = uiState.data
                    audiosUiState.items.find { exoPlayer.currentMediaItem?.mediaId == it.audio.id && it.isPlaying }?.let {
                        updateAudioUiItemState(it.copy(isPlaying = true, playbackPosition = progress), null)
                    }
                }
            }
        }
    }

    fun savePlayingAudioPlaybackPositionState() {
        if (exoPlayer.isPlaying) {
            cancelFlow = true
            val uiState = currentData
            if (uiState is UiState.Success) {
                val audiosUiState = uiState.data
                audiosUiState.items.find { exoPlayer.currentMediaItem?.mediaId == it.audio.id && it.isPlaying }?.let {
                    updateAudioUiItemState(it.copy(isPlaying = false, playbackPosition = exoPlayer.currentPosition), null)
                }
            }
        }
    }

    private fun Player.checkPlaybackPositionFlow(): Flow<Long> = flow {
        while (currentCoroutineContext().isActive && !cancelFlow) {
            if (playbackState != Player.STATE_ENDED)
                emit(currentPosition)
            delay(50L)
        }
    }
        .distinctUntilChanged()
}