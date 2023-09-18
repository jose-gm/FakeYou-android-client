package com.joseg.fakeyouclient.ui.feature.voiceSelection

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joseg.fakeyouclient.ui.shared.UiState
import com.joseg.fakeyouclient.ui.shared.asUiState
import com.joseg.fakeyouclient.common.enums.FilterOptions
import com.joseg.fakeyouclient.common.enums.LanguageTag
import com.joseg.fakeyouclient.common.enums.getFlagIconRes
import com.joseg.fakeyouclient.data.ApiResult
import com.joseg.fakeyouclient.data.combineApiResult
import com.joseg.fakeyouclient.data.map
import com.joseg.fakeyouclient.data.repository.CategoriesRepository
import com.joseg.fakeyouclient.data.repository.VoiceModelsRepository
import com.joseg.fakeyouclient.data.repository.VoiceSettingsRepository
import com.joseg.fakeyouclient.model.Category
import com.joseg.fakeyouclient.model.VoiceModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceSelectionViewModel @Inject constructor(
    private val voiceModelsRepository: VoiceModelsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val voiceSettingsRepository: VoiceSettingsRepository
) : ViewModel() {
    private val _voiceModelsSharedFlow: MutableSharedFlow<ApiResult<List<VoiceModel>>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val _categoriesSharedFlow: MutableSharedFlow<ApiResult<List<Category>>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val _languageTagsSharedFlow: MutableSharedFlow<ApiResult<Set<LanguageTag>>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val _selectedFilterMenuStateFlow = MutableStateFlow(FilterOptions.LANGUAGE)
    private val _checkedItemMapStateFlow: MutableStateFlow<MutableMap<FilterOptions, Any>> =
        MutableStateFlow(mutableMapOf())
    private val _searchQueryStateFlow = MutableStateFlow("")

    val voiceModelUiStateFlow = combine(
        _voiceModelsSharedFlow,
        _checkedItemMapStateFlow,
        _searchQueryStateFlow
    ) { apiVoiceModels, checkedItemMap, searchQuery ->
        apiVoiceModels.map { voiceModels ->
            VoiceModelUiState(
                voiceModels = voiceModels
                    .filter { it.title.lowercase().contains(searchQuery) }
                    .applyFilters(checkedItemMap),
                isFilterActive = checkedItemMap.isNotEmpty()
            )
        }
    }
        .asUiState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = UiState.Loading
        )

    val filterUiStateStateFlow = combine(
        _selectedFilterMenuStateFlow,
        _checkedItemMapStateFlow,
        _languageTagsSharedFlow,
        _categoriesSharedFlow
    ) { selectedFilterType, checkedItemMap, apiLanguageTags, apiCategories ->
        val selectedCategoryCompactFilter = checkedItemMap[FilterOptions.CATEGORY] as? Category
        val selectedLanguageTagFilter = checkedItemMap[FilterOptions.LANGUAGE] as? LanguageTag
        val selectedSubCategoryCompactFilter = checkedItemMap[FilterOptions.SUB_CATEGORY] as? Category

        combineApiResult(apiLanguageTags, apiCategories) { languageTags, categories ->
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

    }
        .asUiState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = UiState.Loading
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
                        _voiceModelsSharedFlow.emit(voiceModels)
                        _languageTagsSharedFlow.emit(
                            voiceModels.map {
                                it.map { voiceModel -> voiceModel.ietfPrimaryLanguageSubtag }.toSet()
                            }
                        )
                    }
            }

            launch {
                categoriesRepository.getCategories(refresh)
                    .collect { categories ->
                        _categoriesSharedFlow.emit(categories)
                    }
            }
        }
    }

    fun submitVoiceModelSelection(voiceModel: VoiceModel) = viewModelScope.launch {
        voiceSettingsRepository.saveVoiceModel(voiceModel)
    }


    fun submitFilterSelection(selectedType: FilterOptions) {
        _selectedFilterMenuStateFlow.value = selectedType
    }

    fun submitCheckItemData(data: Any) {
        val newMap = _checkedItemMapStateFlow.value.toMutableMap()
        when (data) {
            is LanguageTag -> newMap[FilterOptions.LANGUAGE] = data
            is Category -> {
                if ((data.maybeSuperCategoryToken == null && !data.canHaveSubcategories) ||
                    (data.maybeSuperCategoryToken == null && data.subCategories?.isEmpty() == true) ||
                    (data.maybeSuperCategoryToken == null && data.subCategories?.isEmpty() == false))
                    newMap[FilterOptions.CATEGORY] = data
                else
                    newMap[FilterOptions.SUB_CATEGORY]= data
            }
        }
        _checkedItemMapStateFlow.value = newMap
    }

    fun submitSearchQuery(text: String) {
        _searchQueryStateFlow.value = text
    }

    fun resetFilterSelection() {
        _selectedFilterMenuStateFlow.value = FilterOptions.LANGUAGE
    }

    fun resetAllFilters() {
        _selectedFilterMenuStateFlow.value = FilterOptions.LANGUAGE
        _checkedItemMapStateFlow.value = mutableMapOf()
    }

    data class VoiceModelUiState(
        val voiceModels: List<VoiceModel>,
        val isFilterActive: Boolean
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

private fun List<VoiceModel>.applyFilters(
    checkedItemsMap: Map<FilterOptions, Any>
): List<VoiceModel> {
    val languageTag = checkedItemsMap[FilterOptions.LANGUAGE] as? LanguageTag
    val category = checkedItemsMap[FilterOptions.CATEGORY] as? Category
    val subCategory = checkedItemsMap[FilterOptions.SUB_CATEGORY] as? Category

    val filterByLanguage: (VoiceModel) -> Boolean = {
        it.ietfPrimaryLanguageSubtag == languageTag
    }

    val filterByCategory: (VoiceModel) -> Boolean = {
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

    val filterBySubCategories: (VoiceModel) -> Boolean = {
        it.categoryTokens.contains(subCategory?.categoryToken)
    }

    return this
            .filter { if (languageTag == null) true else filterByLanguage(it) }
            .filter { if (category == null) true else filterByCategory(it) }
            .filter { if (subCategory == null) true else filterBySubCategories(it) }
}