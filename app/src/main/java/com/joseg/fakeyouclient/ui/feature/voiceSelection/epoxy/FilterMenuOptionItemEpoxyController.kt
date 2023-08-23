package com.joseg.fakeyouclient.ui.feature.voiceSelection.epoxy

import com.airbnb.epoxy.TypedEpoxyController
import com.joseg.fakeyouclient.ui.feature.voiceSelection.VoiceSelectionViewModel

class FilterMenuOptionItemEpoxyController(
    private val onFilterMenuOptionItemClick: (VoiceSelectionViewModel.FilterMenuOptionItemType) -> Unit
) : TypedEpoxyController<List<VoiceSelectionViewModel.FilterMenuOptionItemUiState>>() {

    override fun buildModels(data: List<VoiceSelectionViewModel.FilterMenuOptionItemUiState>?) {
        data?.forEach {
            FilterMenuOptionItemEpoxyModel(
                it,
                false,
                onFilterMenuOptionItemClick
            )
                .id(it.data.toString())
                .addTo(this)
        }
    }
}