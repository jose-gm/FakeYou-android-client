package com.joseg.fakeyouclient.ui.feature.audioList.epoxy

import android.util.Log
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.airbnb.epoxy.TypedEpoxyController
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.model.Audio
import com.joseg.fakeyouclient.ui.component.epoxymodels.EmptyScreenEpoxyModel
import com.joseg.fakeyouclient.ui.component.epoxymodels.LoadingScreenEpoxyModel
import com.joseg.fakeyouclient.ui.feature.audioList.AudiosViewModel
import com.joseg.fakeyouclient.ui.shared.UiState
import com.joseg.fakeyouclient.ui.shared.UiText
import com.joseg.fakeyouclient.ui.shared.onError
import com.joseg.fakeyouclient.ui.shared.onLoading
import com.joseg.fakeyouclient.ui.shared.onSuccess

class AudiosEpoxyController(
    private val updateAudioUiItemState: (AudiosViewModel.AudioItemUiState, Long?) -> Unit,
    private val isAudioDownloaded: (Audio) -> Boolean,
    private val getAudioFilePath: (Audio) -> String?
) : TypedEpoxyController<UiState<AudiosViewModel.AudiosUiState>>() {
    private lateinit var exoPlayer: ExoPlayer

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
                                getAudioFilePath
                            )
                                .attachPlayer(exoPlayer)
                                .id(it.hashCode())
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
                                updateAudioUiItemState(it.copy(isPlaying = false, lastSavedPlaybackPosition = 0), null)
                            }
                        }
                    }
                }
            }
        })
    }

    fun savePlayingAudioPlaybackPositionState() {
        if (exoPlayer.isPlaying) {
            val uiState = currentData
            if (uiState is UiState.Success) {
                val audiosUiState = uiState.data
                audiosUiState.items.find { exoPlayer.currentMediaItem?.mediaId == it.audio.id && it.isPlaying }?.let {
                    updateAudioUiItemState(it.copy(isPlaying = false, lastSavedPlaybackPosition = exoPlayer.currentPosition), null)
                }
            }
        }

    }
}