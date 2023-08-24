package com.joseg.fakeyouclient.ui.feature.voiceSelection.epoxy

import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.common.epoxy.ViewBindingEpoxyModelWithHolder
import com.joseg.fakeyouclient.databinding.ModelFilterMenuOptionItemBinding
import com.joseg.fakeyouclient.ui.feature.voiceSelection.VoiceSelectionViewModel

data class CheckItemEpoxyModel(
    private val checkItem: VoiceSelectionViewModel.CheckItem,
    private val onClick: (Any) -> Unit
) : ViewBindingEpoxyModelWithHolder<ModelFilterMenuOptionItemBinding>(R.layout.model_filter_menu_option_item) {

    override fun ModelFilterMenuOptionItemBinding.bind() {
        root.setOnClickListener {
            onClick(checkItem.data)
        }

        title.text = checkItem.label
        if (checkItem.icon != null) {
            imageview.isVisible = true
            imageview.setImageResource(checkItem.icon)
        }
        else
            imageview.isGone = true
        checkbox.isChecked = checkItem.isSelected
    }
}