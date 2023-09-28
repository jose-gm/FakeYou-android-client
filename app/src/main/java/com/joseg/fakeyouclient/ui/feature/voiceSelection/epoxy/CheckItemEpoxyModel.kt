package com.joseg.fakeyouclient.ui.feature.voiceSelection.epoxy

import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.databinding.EpoxyModelFilterCheckItemBinding
import com.joseg.fakeyouclient.ui.component.epoxymodels.ViewBindingEpoxyModelWithHolder
import com.joseg.fakeyouclient.ui.feature.voiceSelection.VoiceSelectionViewModel

data class CheckItemEpoxyModel(
    private val checkItem: VoiceSelectionViewModel.CheckItem,
    private val onClick: (Any) -> Unit
) : ViewBindingEpoxyModelWithHolder<EpoxyModelFilterCheckItemBinding>(R.layout.epoxy_model_filter_check_item) {

    override fun EpoxyModelFilterCheckItemBinding.bind() {
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