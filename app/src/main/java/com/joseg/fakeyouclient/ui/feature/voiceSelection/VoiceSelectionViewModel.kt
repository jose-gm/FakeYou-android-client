package com.joseg.fakeyouclient.ui.feature.voiceSelection

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.common.Result
import com.joseg.fakeyouclient.common.asResult
import com.joseg.fakeyouclient.common.enums.FilterMenuOptions
import com.joseg.fakeyouclient.common.enums.LanguageTag
import com.joseg.fakeyouclient.common.enums.getFlagIconRes
import com.joseg.fakeyouclient.common.enums.getStringRes
import com.joseg.fakeyouclient.data.repository.CategoriesRepository
import com.joseg.fakeyouclient.data.repository.VoiceModelsRepository
import com.joseg.fakeyouclient.model.CategoryCompact
import com.joseg.fakeyouclient.model.VoiceModelCompact
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

    private val _selectedFilterMenuStateFlow = MutableStateFlow(FilterMenuOptions.LANGUAGE)
    private val _selectedFilterMenuOptionItemsStateFlow: MutableStateFlow<MutableMap<FilterMenuOptions, FilterMenuOptionItemType>> =
        MutableStateFlow(mutableMapOf())
    private val _searchQueryStateFlow = MutableStateFlow("")

    val voiceModelUiStateFlow = combine(
        _voiceModelsSharedFlow,
        _selectedFilterMenuOptionItemsStateFlow,
        _searchQueryStateFlow
    ) { voiceModels, selectedFilterMenuOptionItemMap, searchQuery ->
        var newVoiceModels = voiceModels
        newVoiceModels = newVoiceModels.filter { it.title.lowercase().contains(searchQuery) }

        selectedFilterMenuOptionItemMap.keys.forEach { filterMenuOption ->

            val predicate: (VoiceModelCompact) -> Boolean = when (val filterMenuOptionItem = selectedFilterMenuOptionItemMap[filterMenuOption]) {
                is FilterMenuOptionItemType.FilterMenuOptionOptionItemLanguage -> {
                    { voiceModel -> voiceModel.ietfPrimaryLanguageSubtag == filterMenuOptionItem.data }
                }
                is FilterMenuOptionItemType.FilterMenuOptionOptionItemCategory -> {
                    { voiceModel ->
                        if (filterMenuOptionItem.data.subCategories.isNullOrEmpty())
                            voiceModel.categoryTokens.contains(filterMenuOptionItem.data.categoryToken)
                        else
                            voiceModel.categoryTokens.any { token ->
                                filterMenuOptionItem.data.subCategories
                                    .map { subCategory -> subCategory.categoryToken }
                                    .contains(token)
                            }
                    }
                }
                is FilterMenuOptionItemType.FilterMenuOptionOptionItemSubCategory -> {
                    { voiceModel -> voiceModel.categoryTokens.contains(filterMenuOptionItem.data.categoryToken) }
                }
                else -> { { true } }
            }
            newVoiceModels = newVoiceModels.filter { predicate(it) }
        }

        VoiceModelUiState(voiceModels = newVoiceModels)
    }
        .asResult()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Result.Loading
        )

    private val filterOptionsUiStateMap: MutableMap<FilterMenuOptions, FilterMenuOptionUiState> = mutableMapOf()

    val filterOptionsUiStateFlow: StateFlow<Result<FilterUiState>> = combine(
        _languageTagsSharedFlow,
        _categoriesSharedFlow,
        _selectedFilterMenuStateFlow,
        _selectedFilterMenuOptionItemsStateFlow
    ) { languageTags, categories, selectedFilterMenu, selectedFilterMenuOptionItemMap ->
        filterOptionsUiStateMap.putIfAbsent(
            FilterMenuOptions.LANGUAGE,
            FilterMenuOptionUiState(
                icon = R.drawable.ic_baseline_language_24,
                title = UiText.TextResource(FilterMenuOptions.LANGUAGE.getStringRes()),
                type = FilterMenuOptions.LANGUAGE,
                items = languageTags.map { languageTag ->
                    FilterMenuOptionItemUiState(
                        data = FilterMenuOptionItemType.FilterMenuOptionOptionItemLanguage(languageTag),
                        icon = languageTag.getFlagIconRes(),
                        isSelected = selectedFilterMenuOptionItemMap[FilterMenuOptions.LANGUAGE]
                            .let {
                                (it as? FilterMenuOptionItemType.FilterMenuOptionOptionItemLanguage)?.data == languageTag
                            }
                    )
                }
            )
        )

        filterOptionsUiStateMap.putIfAbsent(
            FilterMenuOptions.CATEGORY,
            FilterMenuOptionUiState(
                icon = R.drawable.ic_baseline_category_24,
                title = UiText.TextResource(FilterMenuOptions.CATEGORY.getStringRes()),
                type = FilterMenuOptions.CATEGORY,
                items = categories
                    .filter { it.maybeSuperCategoryToken == null }
                    .map { categoryCompact ->
                        FilterMenuOptionItemUiState(
                            data = FilterMenuOptionItemType.FilterMenuOptionOptionItemCategory(categoryCompact),
                            icon = null,
                            isSelected = false
                        )
                    }
            )
        )

        (selectedFilterMenuOptionItemMap[FilterMenuOptions.CATEGORY] as? FilterMenuOptionItemType.FilterMenuOptionOptionItemCategory)
            ?.data
            ?.let { selectedCategoryItem ->
                selectedCategoryItem.subCategories?.let { subCategoriesItem ->
                    if (subCategoriesItem.isEmpty()) {
                        filterOptionsUiStateMap.remove(FilterMenuOptions.SUB_CATEGORY)
                        return@let
                    }

                    if (selectedCategoryItem.nameForDropdown !=
                        (filterOptionsUiStateMap[FilterMenuOptions.SUB_CATEGORY]?.title as? UiText.DynamicText)?.text)
                        filterOptionsUiStateMap[FilterMenuOptions.SUB_CATEGORY] = FilterMenuOptionUiState(
                                title = UiText.DynamicText(selectedCategoryItem.nameForDropdown),
                                icon = R.drawable.ic_baseline_category_24,
                                type = FilterMenuOptions.SUB_CATEGORY,
                                items = subCategoriesItem
                                    .map { categoryCompact ->
                                        FilterMenuOptionItemUiState(
                                            data = FilterMenuOptionItemType.FilterMenuOptionOptionItemSubCategory(categoryCompact),
                                            icon = null,
                                            isSelected = false
                                        )
                                    }
                            )
                }
            } ?: filterOptionsUiStateMap.remove(FilterMenuOptions.SUB_CATEGORY)

        val newMap = filterOptionsUiStateMap.toMutableMap()
        val selectedFilterMenuOption = filterOptionsUiStateMap[selectedFilterMenu]
        selectedFilterMenuOption?.let {
            newMap[selectedFilterMenu] = it.copy(
                items = it.items.map { filterMenuOptionItem ->
                    if (filterMenuOptionItem.data == selectedFilterMenuOptionItemMap[selectedFilterMenu])
                        filterMenuOptionItem.copy(isSelected = true)
                    else
                        filterMenuOptionItem.copy(isSelected = false)
                }
            )
        }

        FilterUiState(
            newMap.map { it.value },
            selectedFilterMenu
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
                        _voiceModelsSharedFlow.tryEmit(voiceModels)
                        _languageTagsSharedFlow.tryEmit(
                            voiceModels.map { it.ietfPrimaryLanguageSubtag }.toSet()
                        )
                    }
            }

            launch {
                categoriesRepository.getCategories(refresh)
                    .collect { categories ->
                        _categoriesSharedFlow.tryEmit(categories)
                    }
            }
        }
    }

    fun setSelectedFilterMenuOption(selectedType: FilterMenuOptions) {
        _selectedFilterMenuStateFlow.value = selectedType
    }

    fun resetFilterMenuOptionSelection() {
        _selectedFilterMenuStateFlow.value = FilterMenuOptions.LANGUAGE
    }

    fun setSelectedFilterMenuOptionItem(filterMenuOptionItemType: FilterMenuOptionItemType) {
        val oldMap = _selectedFilterMenuOptionItemsStateFlow.value.toMutableMap()
        when (filterMenuOptionItemType) {
            is FilterMenuOptionItemType.FilterMenuOptionOptionItemLanguage -> oldMap[FilterMenuOptions.LANGUAGE] = filterMenuOptionItemType
            is FilterMenuOptionItemType.FilterMenuOptionOptionItemCategory -> oldMap[FilterMenuOptions.CATEGORY] = filterMenuOptionItemType
            is FilterMenuOptionItemType.FilterMenuOptionOptionItemSubCategory -> oldMap[FilterMenuOptions.SUB_CATEGORY] = filterMenuOptionItemType
        }
        _selectedFilterMenuOptionItemsStateFlow.value = oldMap
    }

    fun resetAllFilters() {
        _selectedFilterMenuStateFlow.value = FilterMenuOptions.LANGUAGE
        _selectedFilterMenuOptionItemsStateFlow.value = mutableMapOf()
    }

    fun submitSearchQuery(text: String) {
        _searchQueryStateFlow.value = text
    }

    data class VoiceModelUiState(
        val voiceModels: List<VoiceModelCompact>,
    )

    data class FilterUiState(
        val filterMenusUiState: List<FilterMenuOptionUiState>,
        val selectedFilterMenuOption: FilterMenuOptions
    )

    data class FilterMenuOptionUiState(
        val title: UiText,
        @DrawableRes val icon: Int,
        val type: FilterMenuOptions,
        val items: List<FilterMenuOptionItemUiState>,
    )

    data class FilterMenuOptionItemUiState(
        val data: FilterMenuOptionItemType,
        @DrawableRes val icon: Int?,
        val isSelected: Boolean
    )

    private interface FilterMenuOptionItemData<T> {
        val data: T
    }

    sealed class FilterMenuOptionItemType(val description: String) {
        class FilterMenuOptionOptionItemLanguage(
            override val data: LanguageTag,
        ) : FilterMenuOptionItemData<LanguageTag>, FilterMenuOptionItemType(data.name.uppercase())

        class FilterMenuOptionOptionItemCategory(
            override val data: CategoryCompact,
        ) : FilterMenuOptionItemData<CategoryCompact>, FilterMenuOptionItemType(data.nameForDropdown)

        class FilterMenuOptionOptionItemSubCategory(
            override val data: CategoryCompact,
        ) : FilterMenuOptionItemData<CategoryCompact>, FilterMenuOptionItemType(data.nameForDropdown)
    }
}