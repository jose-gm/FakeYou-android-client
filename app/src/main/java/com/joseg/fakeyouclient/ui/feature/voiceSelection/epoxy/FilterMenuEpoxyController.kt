package com.joseg.fakeyouclient.ui.feature.voiceSelection.epoxy

import com.airbnb.epoxy.TypedEpoxyController
import com.joseg.fakeyouclient.common.enums.FilterMenuOptions
import com.joseg.fakeyouclient.ui.feature.voiceSelection.VoiceSelectionViewModel

class FilterMenuEpoxyController(
    private val onFilterMenuOptionClick: (FilterMenuOptions) -> Unit
) : TypedEpoxyController<VoiceSelectionViewModel.FilterUiState>() {

    override fun buildModels(data: VoiceSelectionViewModel.FilterUiState?) {
        data?.filterMenusUiState?.forEach { filterOptionUiState ->
            FilterMenuOptionEpoxyModel(
                filterOptionUiState,
                data.selectedFilterMenuOption == filterOptionUiState.type,
                onClick =onFilterMenuOptionClick
            )
                .id(filterOptionUiState.hashCode())
                .addTo(this)
        }
    }
}