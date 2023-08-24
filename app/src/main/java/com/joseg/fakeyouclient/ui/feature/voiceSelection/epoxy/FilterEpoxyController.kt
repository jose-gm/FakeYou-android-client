package com.joseg.fakeyouclient.ui.feature.voiceSelection.epoxy

import com.airbnb.epoxy.TypedEpoxyController
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.common.enums.FilterOptions
import com.joseg.fakeyouclient.ui.feature.voiceSelection.VoiceSelectionViewModel
import com.joseg.fakeyouclient.ui.utils.UiText

class FilterEpoxyController(
    private val onFilterMenuOptionClick: (FilterOptions) -> Unit
) : TypedEpoxyController<VoiceSelectionViewModel.FilterUiState>() {

    override fun buildModels(data: VoiceSelectionViewModel.FilterUiState?) {
        data?.let {
            FilterOptionEpoxyModel(
                label = UiText.TextResource(R.string.Language),
                icon = R.drawable.ic_baseline_language_24,
                isSelected = it.selectedFilter == FilterOptions.LANGUAGE,
                type = FilterOptions.LANGUAGE,
                onClick = onFilterMenuOptionClick
            )
                .id("language_filter")
                .addTo(this)

            FilterOptionEpoxyModel(
                label = UiText.TextResource(R.string.Category),
                icon = R.drawable.ic_baseline_category_24,
                isSelected = it.selectedFilter == FilterOptions.CATEGORY,
                FilterOptions.CATEGORY,
                onClick = onFilterMenuOptionClick
            )
                .id("category_filter")
                .addTo(this)

            FilterOptionEpoxyModel(
                label = UiText.DynamicText(it.subCategoryLabel),
                icon = R.drawable.ic_baseline_category_24,
                isSelected = it.selectedFilter == FilterOptions.SUB_CATEGORY,
                FilterOptions.SUB_CATEGORY,
                onClick = onFilterMenuOptionClick
            )
                .id("sub-category_filter")
                .addIf(data.showSubCategory, this)
        }
    }
}