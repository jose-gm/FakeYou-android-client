package com.joseg.fakeyouclient.ui.feature.audioList.epoxy

import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.databinding.EpoxyModelAudioItemBinding
import com.joseg.fakeyouclient.ui.component.epoxymodels.ViewBindingEpoxyModelWithHolder
import com.joseg.fakeyouclient.ui.feature.audioList.AudiosViewModel

data class AudioItemEpoxyModel(
    val audioItemUiState: AudiosViewModel.AudioItemUiState,
    private val updateAudioUiItemState: (AudiosViewModel.AudioItemUiState, Long?) -> Unit,
    private val isAudioDownloaded: (AudiosViewModel.AudioItemUiState) -> Boolean,
    private val getAudioFilePath: (AudiosViewModel.AudioItemUiState) -> String?,
) : ViewBindingEpoxyModelWithHolder<EpoxyModelAudioItemBinding>(R.layout.epoxy_model_audio_item) {
    private lateinit var exoPlayer: ExoPlayer
    private val mediaMetadataRetriever = MediaMetadataRetriever()

    override fun EpoxyModelAudioItemBinding.bind() {
        inferenceTextTextView.text = audioItemUiState.audio.inferenceText
        voiceModelNameTextView.text = audioItemUiState.audio.voiceModelName
        waveformSeekBar.setSampleFrom(audioItemUiState.audio.sample)

        if (isAudioDownloaded(audioItemUiState)) {
            mediaMetadataRetriever.setDataSource(getAudioFilePath(audioItemUiState))
            val timeInMilliSec = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()!!
            val progressTimeText = if (audioItemUiState.playbackPosition > 0)
                convertMilliSecsToMinutesWithSecondsString(audioItemUiState.playbackPosition)
            else
                convertMilliSecsToMinutesWithSecondsString(timeInMilliSec)

            durationTextView.text = progressTimeText
            waveformSeekBar.maxProgress = timeInMilliSec.toFloat()
            waveformSeekBar.progress = audioItemUiState.playbackPosition.toFloat()

            playPauseButton.icon = if (audioItemUiState.isPlaying)
                AppCompatResources.getDrawable(root.context, R.drawable.ic_baseline_pause)
            else
                AppCompatResources.getDrawable(root.context, R.drawable.ic_baseline_play_arrow)

            playPauseButton.setOnClickListener {
                if (audioItemUiState.isPlaying) {
                    updateAudioUiItemState(audioItemUiState.copy(isPlaying = false, playbackPosition = exoPlayer.currentPosition), null)
                    pauseAudio()
                } else {
                    updateAudioUiItemState(audioItemUiState.copy(isPlaying = true), exoPlayer.currentPosition)
                    if (exoPlayer.currentMediaItem?.mediaId != audioItemUiState.audio.id)
                        updateMediaItem()
                    playAudio()
                }
            }
        } else {
            durationTextView.text = "-:--"
            waveformSeekBar.progress = 0f

            downloadButton.isVisible = true
            playPauseButton.isGone = true

            downloadButton.setOnClickListener {

            }
        }
    }

    private fun playAudio() {
        if (audioItemUiState.playbackPosition > 0 && exoPlayer.playbackState != Player.STATE_ENDED)
            exoPlayer.seekTo(audioItemUiState.playbackPosition)
        Util.handlePlayButtonAction(exoPlayer)
    }

    private fun pauseAudio() {
        Util.handlePauseButtonAction(exoPlayer)
    }

    private fun getMediaItem(): MediaItem = MediaItem.Builder()
        .setMediaId(audioItemUiState.audio.id)
        .setUri(Uri.parse(getAudioFilePath(audioItemUiState)))
        .build()

    private fun updateMediaItem() {
        exoPlayer.setMediaItem(getMediaItem())
        exoPlayer.prepare()
    }

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