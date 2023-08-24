package com.joseg.fakeyouclient.ui.feature.voiceSelection

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.common.Result
import com.joseg.fakeyouclient.common.asResult
import com.joseg.fakeyouclient.common.enums.FilterOptions
import com.joseg.fakeyouclient.common.enums.LanguageTag
import com.joseg.fakeyouclient.common.enums.getFlagIconRes
import com.joseg.fakeyouclient.common.enums.getStringRes
import com.joseg.fakeyouclient.data.repository.CategoriesRepository
import com.joseg.fakeyouclient.data.repository.VoiceModelsRepository
import com.joseg.fakeyouclient.model.CategoryCompact
import com.joseg.fakeyouclient.model.VoiceModelCompact
import com.joseg.fakeyouclient.model.asCategoriesCompact
import com.joseg.fakeyouclient.model.asVoiceModelsCompact
import com.joseg.fakeyouclient.ui.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceSelectionViewModel @Inject constructor(
    private val voiceModelsRepository: VoiceModelsRepository,
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {
    private val _voiceModelsSharedFlow: MutableSharedFlow<List<VoiceModelCompact>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val _categoriesSharedFlow: MutableSharedFlow<List<CategoryCompact>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val _languageTagsSharedFlow: MutableSharedFlow<Set<LanguageTag>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val _selectedFilterMenuStateFlow = MutableStateFlow(FilterOptions.LANGUAGE)
    private val _checkedItemMapFlow: MutableStateFlow<MutableMap<FilterOptions, Any>> =
        MutableStateFlow(mutableMapOf())
    private val _searchQueryStateFlow = MutableStateFlow("")

    val voiceModelUiStateFlow = combine(
        _voiceModelsSharedFlow,
        _checkedItemMapFlow,
        _searchQueryStateFlow
    ) { voiceModels, checkedItemMap, searchQuery ->
        VoiceModelUiState(
            voiceModels = voiceModels
                .filter { it.title.lowercase().contains(searchQuery) }
                .applyFilters(checkedItemMap)
        )
    }
        .asResult()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Result.Loading
        )

    val filterUiStateStateFlow = combine(
        _selectedFilterMenuStateFlow,
        _checkedItemMapFlow,
        _languageTagsSharedFlow,
        _categoriesSharedFlow
    ) { selectedFilterType, checkedItemMap, languageTags, categories ->
        val selectedCategoryCompactFilter = checkedItemMap[FilterOptions.CATEGORY] as? CategoryCompact
        val selectedLanguageTagFilter = checkedItemMap[FilterOptions.LANGUAGE] as? LanguageTag
        val selectedSubCategoryCompactFilter = checkedItemMap[FilterOptions.SUB_CATEGORY] as? CategoryCompact

        FilterUiState(
            selectedFilter = selectedFilterType,
            showSubCategory = selectedCategoryCompactFilter?.subCategories?.isNotEmpty() == true,
            subCategoryLabel = selectedCategoryCompactFilter?.nameForDropdown ?: "",
            checkItems = when (selectedFilterType) {
                FilterOptions.LANGUAGE -> {
                    languageTags.map { CheckItem(
                        label = it.name.uppercase(),
                        icon = it.getFlagIconRes(),
                        data = it,
                        isSelected = selectedLanguageTagFilter == it
                    ) }
                }
                FilterOptions.CATEGORY -> {
                    categories
                        .filter { it.maybeSuperCategoryToken == null }
                        .map { CheckItem(
                        label = it.nameForDropdown,
                        icon = null,
                        data = it,
                        isSelected = selectedCategoryCompactFilter == it
                    ) }
                }
                FilterOptions.SUB_CATEGORY -> {
                    selectedCategoryCompactFilter?.subCategories
                        ?.map {
                            CheckItem(
                                label = it.nameForDropdown,
                                icon = null,
                                data = it,
                                isSelected = selectedSubCategoryCompactFilter == it
                            )
                        } ?: emptyList()
                }
            }
        )
    }
        .asResult()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Result.Loading
        )

    init {
        loadData()
    }

    fun refresh() {
        loadData(true)
    }

    private fun loadData(refresh: Boolean = false) {
        viewModelScope.launch {
            launch {
                voiceModelsRepository.getVoiceModels(refresh)
                    .collect { voiceModels ->
                        _voiceModelsSharedFlow.tryEmit(voiceModels.asVoiceModelsCompact())
                        _languageTagsSharedFlow.tryEmit(
                            voiceModels.map { it.ietfPrimaryLanguageSubtag }.toSet()
                        )
                    }
            }

            launch {
                categoriesRepository.getCategories(refresh)
                    .collect { categories ->
                        _categoriesSharedFlow.tryEmit(categories.asCategoriesCompact())
                    }
            }
        }
    }

    fun submitFilterSelection(selectedType: FilterOptions) {
        _selectedFilterMenuStateFlow.value = selectedType
    }

    fun submitCheckItemData(data: Any) {
        val newMap = _checkedItemMapFlow.value.toMutableMap()
        when (data) {
            is LanguageTag -> newMap[FilterOptions.LANGUAGE] = data
            is CategoryCompact -> {
                if ((data.maybeSuperCategoryToken == null && !data.canHaveSubcategories) ||
                    (data.maybeSuperCategoryToken == null && data.subCategories?.isEmpty() == true) ||
                    (data.maybeSuperCategoryToken == null && data.subCategories?.isEmpty() == false))
                    newMap[FilterOptions.CATEGORY] = data
                else
                    newMap[FilterOptions.SUB_CATEGORY]= data
            }
        }
        _checkedItemMapFlow.value = newMap
    }

    fun submitSearchQuery(text: String) {
        _searchQueryStateFlow.value = text
    }

    fun resetFilterSelection() {
        _selectedFilterMenuStateFlow.value = FilterOptions.LANGUAGE
    }

    fun resetAllFilters() {
        _selectedFilterMenuStateFlow.value = FilterOptions.LANGUAGE
        _checkedItemMapFlow.value = mutableMapOf()
    }

    data class VoiceModelUiState(
        val voiceModels: List<VoiceModelCompact>,
    )

    data class FilterUiState(
        val selectedFilter: FilterOptions,
        val showSubCategory: Boolean,
        val subCategoryLabel: String,
        val checkItems: List<CheckItem>
    )

    data class CheckItem(
        val label: String,
        @DrawableRes val icon: Int?,
        val data: Any,
        val isSelected: Boolean
    )
}

private fun List<VoiceModelCompact>.applyFilters(
    checkedItemsMap: Map<FilterOptions, Any>
): List<VoiceModelCompact> {
    val languageTag = checkedItemsMap[FilterOptions.LANGUAGE] as? LanguageTag
    val category = checkedItemsMap[FilterOptions.CATEGORY] as? CategoryCompact
    val subCategory = checkedItemsMap[FilterOptions.SUB_CATEGORY] as? CategoryCompact

    val filterByLanguage: (VoiceModelCompact) -> Boolean = {
        it.ietfPrimaryLanguageSubtag == languageTag
    }

    val filterByCategory: (VoiceModelCompact) -> Boolean = {
        if (category?.subCategories?.isEmpty() == true) {
            it.categoryTokens.contains(category.categoryToken)
        } else {
            it.categoryTokens.any { token ->
                category?.subCategories
                    ?.map { subCategory -> subCategory.categoryToken }
                    ?.contains(token) ?: false
            }
        }
    }

    val filterBySubCategories: (VoiceModelCompact) -> Boolean = {
        it.categoryTokens.contains(subCategory?.categoryToken)
    }

    return this
            .filter { if (languageTag == null) true else filterByLanguage(it) }
            .filter { if (category == null) true else filterByCategory(it) }
            .filter { if (subCategory == null) true else filterBySubCategories(it) }
}