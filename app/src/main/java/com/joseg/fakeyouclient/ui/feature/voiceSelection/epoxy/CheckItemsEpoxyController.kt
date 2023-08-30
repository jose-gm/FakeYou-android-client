package com.joseg.fakeyouclient.ui.feature.voiceSelection.epoxy

import com.airbnb.epoxy.TypedEpoxyController
import com.joseg.fakeyouclient.ui.feature.voiceSelection.VoiceSelectionViewModel

class CheckItemsEpoxyController(
    private val onCheckItemClick: (Any) -> Unit
) : TypedEpoxyController<List<VoiceSelectionViewModel.CheckItem>>() {

    override fun buildModels(data: List<VoiceSelectionViewModel.CheckItem>?) {
        data?.forEach {
            CheckItemEpoxyModel(
                it,
                onCheckItemClick
            )
                .id(it.data.toString())
                .addTo(this)
        }
    }
}