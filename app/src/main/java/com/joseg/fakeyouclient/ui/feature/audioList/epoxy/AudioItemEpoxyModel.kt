package com.joseg.fakeyouclient.ui.feature.audioList.epoxy

import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.appcompat.content.res.AppCompatResources
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.databinding.EpoxyModelAudioItemBinding
import com.joseg.fakeyouclient.model.Audio
import com.joseg.fakeyouclient.ui.component.epoxymodels.ViewBindingEpoxyModelWithHolder
import com.joseg.fakeyouclient.ui.feature.audioList.AudiosViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive

data class AudioItemEpoxyModel(
    val audioItemUiState: AudiosViewModel.AudioItemUiState,
    private val updateAudioUiItemState: (AudiosViewModel.AudioItemUiState, Long?) -> Unit,
    private val isAudioDownloaded: (Audio) -> Boolean,
    private val getAudioFilePath: (Audio) -> String?
) : ViewBindingEpoxyModelWithHolder<EpoxyModelAudioItemBinding>(R.layout.epoxy_model_audio_item) {
    private lateinit var exoPlayer: ExoPlayer
    private val mediaMetadataRetriever = MediaMetadataRetriever()
    private var scope: CoroutineScope? = null

    override fun EpoxyModelAudioItemBinding.unbind() {
        scope?.cancel()
        scope = null
    }

    override fun EpoxyModelAudioItemBinding.bind() {
        scope = CoroutineScope(Dispatchers.Main.immediate)

        inferenceTextTextView.text = audioItemUiState.audio.inferenceText
        voiceModelNameTextView.text = audioItemUiState.audio.voiceModelName
        waveformSeekBar.setSampleFrom(audioItemUiState.audio.sample)

        if (isAudioDownloaded(audioItemUiState.audio)) {
            mediaMetadataRetriever.setDataSource(getAudioFilePath(audioItemUiState.audio))
            val timeInMilliSec = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()!!
            val progressTimeText = if (audioItemUiState.lastSavedPlaybackPosition > 0)
                convertMilliSecsToMinutesWithSecondsString(audioItemUiState.lastSavedPlaybackPosition)
            else
                convertMilliSecsToMinutesWithSecondsString(timeInMilliSec)

            durationTextView.text = progressTimeText

            waveformSeekBar.maxProgress = timeInMilliSec.toFloat()
            waveformSeekBar.progress = audioItemUiState.lastSavedPlaybackPosition.toFloat()

            if (audioItemUiState.isPlaying) {
                exoPlayer.checkPlaybackPositionFlow()
                    .onEach { progress ->
                        waveformSeekBar.progress = progress.toFloat()
                        durationTextView.text = convertMilliSecsToMinutesWithSecondsString(progress)
                    }
                    .launchIn(scope!!)
            }
        } else {
            durationTextView.text = "-:--"
            waveformSeekBar.progress = 0f
        }

        playPauseButton.icon = if (audioItemUiState.isPlaying)
             AppCompatResources.getDrawable(root.context, R.drawable.ic_baseline_pause)
        else
            AppCompatResources.getDrawable(root.context, R.drawable.ic_baseline_play_arrow)

        playPauseButton.setOnClickListener {
            if (audioItemUiState.isPlaying) {
                updateAudioUiItemState(audioItemUiState.copy(isPlaying = false, lastSavedPlaybackPosition = exoPlayer.currentPosition), null)
                pauseAudio()
            } else {
                updateAudioUiItemState(audioItemUiState.copy(isPlaying = true), exoPlayer.currentPosition)
                if (exoPlayer.currentMediaItem?.mediaId != audioItemUiState.audio.id)
                    updateMediaItem()
                playAudio()
            }
        }
    }

    private fun playAudio() {
        if (audioItemUiState.lastSavedPlaybackPosition > 0 && exoPlayer.playbackState != Player.STATE_ENDED)
            exoPlayer.seekTo(audioItemUiState.lastSavedPlaybackPosition)
        Util.handlePlayButtonAction(exoPlayer)
    }

    private fun pauseAudio() {
        Util.handlePauseButtonAction(exoPlayer)
    }

    private fun getMediaItem(): MediaItem = MediaItem.Builder()
        .setMediaId(audioItemUiState.audio.id)
        .setUri(Uri.parse(getAudioFilePath(audioItemUiState.audio)))
        .build()

    private fun updateMediaItem() {
        exoPlayer.setMediaItem(getMediaItem())
        exoPlayer.prepare()
    }

    private fun Player.checkPlaybackPositionFlow(): Flow<Long> = flow {
        while (playbackState != Player.STATE_ENDED && currentCoroutineContext().isActive) {
            emit(currentPosition)
            delay(50L)
        }
    }
        .distinctUntilChanged()

    fun attachPlayer(player: ExoPlayer): AudioItemEpoxyModel {
        exoPlayer = player
        return this
    }

    private fun convertMilliSecsToMinutesWithSecondsString(milliSecs: Long): String {
        val timeInSeconds = milliSecs / 1000
        val hours = timeInSeconds / 3600
        val minutes = (timeInSeconds - hours * 3600) / 60
        val seconds =  timeInSeconds - (hours * 3600 + minutes * 60)

        return "$minutes:${ if (seconds < 10) "0$seconds" else seconds }"
    }
}