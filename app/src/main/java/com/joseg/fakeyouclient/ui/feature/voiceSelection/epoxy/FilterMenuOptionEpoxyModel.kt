package com.joseg.fakeyouclient.ui.feature.voiceSelection.epoxy

import androidx.appcompat.content.res.AppCompatResources
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.common.enums.FilterMenuOptions
import com.joseg.fakeyouclient.common.epoxy.ViewBindingEpoxyModelWithHolder
import com.joseg.fakeyouclient.databinding.ModelFilterMenuOptionBinding
import com.joseg.fakeyouclient.ui.feature.voiceSelection.VoiceSelectionViewModel
import com.joseg.fakeyouclient.ui.utils.UiText

data class FilterMenuOptionEpoxyModel(
    private val filterMenuOptionUiState: VoiceSelectionViewModel.FilterMenuOptionUiState,
    private val isSelected: Boolean,
    private val onClick: (FilterMenuOptions) -> Unit
) : ViewBindingEpoxyModelWithHolder<ModelFilterMenuOptionBinding>(R.layout.model_filter_menu_option) {

    override fun ModelFilterMenuOptionBinding.bind() {
        root.setOnClickListener {
            onClick(filterMenuOptionUiState.type)
        }

        title.text = when (val value = filterMenuOptionUiState.title) {
            is UiText.DynamicText -> value.text
            is UiText.TextResource -> root.context.getString(value.stringRes)
        }

        imageview.setImageDrawable(AppCompatResources.getDrawable(root.context, filterMenuOptionUiState.icon))
        if (isSelected)
            select()
        else
            unselect()
    }

    private fun ModelFilterMenuOptionBinding.select() {
        root.setBackgroundResource(R.drawable.background_selected_filter_menu)
        title.setTextColor(root.context.getColor(R.color.black))
    }

    private fun ModelFilterMenuOptionBinding.unselect() {
        root.background = null
        title.setTextColor(root.context.getColor(R.color.white))
    }
}