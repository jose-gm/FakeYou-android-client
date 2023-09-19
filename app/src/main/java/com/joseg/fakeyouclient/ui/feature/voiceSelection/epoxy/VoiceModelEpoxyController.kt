package com.joseg.fakeyouclient.ui.feature.voiceSelection.epoxy

import com.airbnb.epoxy.EpoxyAsyncUtil
import com.airbnb.epoxy.TypedEpoxyController
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.ui.shared.UiState
import com.joseg.fakeyouclient.ui.shared.onError
import com.joseg.fakeyouclient.ui.shared.onLoading
import com.joseg.fakeyouclient.ui.shared.onSuccess
import com.joseg.fakeyouclient.model.VoiceModel
import com.joseg.fakeyouclient.ui.feature.voiceSelection.VoiceSelectionViewModel
import com.joseg.fakeyouclient.ui.component.epoxymodels.EmptyScreenEpoxyModel
import com.joseg.fakeyouclient.ui.component.epoxymodels.ErrorScreenEpoxyModel
import com.joseg.fakeyouclient.ui.component.epoxymodels.LoadingScreenEpoxyModel
import com.joseg.fakeyouclient.ui.shared.UiText

class VoiceModelEpoxyController(
    private val onVoiceModelClick: (VoiceModel) -> Unit,
    private val onRetry: () -> Unit
) : TypedEpoxyController<UiState<VoiceSelectionViewModel.VoiceModelUiState>>(
    EpoxyAsyncUtil.getAsyncBackgroundHandler(),
    EpoxyAsyncUtil.getAsyncBackgroundHandler()
) {

    override fun buildModels(data: UiState<VoiceSelectionViewModel.VoiceModelUiState>?) {
        data?.let { result ->
            result.onSuccess { voiceModelUiState ->
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
            }.onError { _, _ ->
                ErrorScreenEpoxyModel(onRetry)
                    .id("error_model")
                    .addTo(this)
            }
        }
    }
}