package com.joseg.fakeyouclient.ui.feature.voiceSelection.epoxy

import android.util.Log
import com.airbnb.epoxy.TypedEpoxyController
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.common.UiState
import com.joseg.fakeyouclient.common.onError
import com.joseg.fakeyouclient.common.onLoading
import com.joseg.fakeyouclient.common.onSuccess
import com.joseg.fakeyouclient.model.VoiceModel
import com.joseg.fakeyouclient.ui.feature.voiceSelection.VoiceSelectionViewModel
import com.joseg.fakeyouclient.ui.epoxymodels.EmptyScreenEpoxyModel
import com.joseg.fakeyouclient.ui.epoxymodels.ErrorScreenEpoxyModel
import com.joseg.fakeyouclient.ui.epoxymodels.LoadingScreenEpoxyModel
import com.joseg.fakeyouclient.ui.utils.UiText

class VoiceModelEpoxyController(
    private val onVoiceModelClick: (VoiceModel) -> Unit,
    private val onRetry: () -> Unit
) : TypedEpoxyController<UiState<VoiceSelectionViewModel.VoiceModelUiState>>() {

    override fun buildModels(data: UiState<VoiceSelectionViewModel.VoiceModelUiState>?) {
        data?.let { result ->
            result.onSuccess { voiceModelUiState ->
                Log.e("error-result", "It reached onSuccess in the UI")
                if (voiceModelUiState.voiceModels.isEmpty()) {
                    EmptyScreenEpoxyModel(
                        title = UiText.TextResource(R.string.voice_selection_empty_screen_title),
                        message = UiText.TextResource(R.string.voice_selection_empty_screen_message),
                        drawableRes = R.drawable.ic_sound_wave_3
                    )
                        .id("empty_model")
                        .addTo(this)
                } else {
                    voiceModelUiState.voiceModels.forEach {
                        VoiceModelEpoxyModel(
                            voiceModel = it,
                            onClick = onVoiceModelClick
                        )
                            .id(it.modelToken)
                            .addTo(this)
                    }
                }
            }.onLoading {
               LoadingScreenEpoxyModel()
                   .id("loading_model")
                   .addTo(this)
            }.onError {
                Log.e("error-result", it?.message.toString())
                ErrorScreenEpoxyModel(onRetry)
                    .id("error_model")
                    .addTo(this)
            }
        }
    }
}