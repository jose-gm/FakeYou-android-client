package com.joseg.fakeyouclient.ui.feature.voiceSelection.epoxy

import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyModel
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.common.epoxy.ViewBindingEpoxyModelWithHolder
import com.joseg.fakeyouclient.common.epoxy.ViewBindingHolder
import com.joseg.fakeyouclient.databinding.ModelFilterMenuOptionItemBinding
import com.joseg.fakeyouclient.ui.feature.voiceSelection.VoiceSelectionViewModel

data class FilterMenuOptionItemEpoxyModel(
    private val filterMenuOptionItemUiState: VoiceSelectionViewModel.FilterMenuOptionItemUiState,
    private val isSelected: Boolean,
    private val onClick: (VoiceSelectionViewModel.FilterMenuOptionItemType) -> Unit
) : ViewBindingEpoxyModelWithHolder<ModelFilterMenuOptionItemBinding>(R.layout.model_filter_menu_option_item) {

    override fun ModelFilterMenuOptionItemBinding.bind() {
        root.setOnClickListener {
            onClick(filterMenuOptionItemUiState.data)
        }

        title.text = filterMenuOptionItemUiState.data.description
        if (filterMenuOptionItemUiState.icon != null) {
            imageview.isVisible = true
            imageview.setImageResource(filterMenuOptionItemUiState.icon)
        }
        else
            imageview.isGone = true

        filterMenuOptionItemUiState.icon
        checkbox.isChecked = filterMenuOptionItemUiState.isSelected
    }

    override fun bind(holder: ViewBindingHolder, previouslyBoundModel: EpoxyModel<*>) {
        super.bind(holder, previouslyBoundModel)
    }
}