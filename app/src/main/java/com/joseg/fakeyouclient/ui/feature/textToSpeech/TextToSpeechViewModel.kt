package com.joseg.fakeyouclient.ui.feature.textToSpeech

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joseg.fakeyouclient.common.Result
import com.joseg.fakeyouclient.common.asResult
import com.joseg.fakeyouclient.data.repository.CategoriesRepository
import com.joseg.fakeyouclient.data.repository.TtsRequestRepository
import com.joseg.fakeyouclient.data.repository.VoiceModelsRepository
import com.joseg.fakeyouclient.model.ChildCategoryCompact
import com.joseg.fakeyouclient.model.ParentCategoryCompat
import com.joseg.fakeyouclient.model.VoiceModelCompact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TextToSpeechViewModel @Inject constructor(
    private val voiceModelsRepository: VoiceModelsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val ttsRequestRepository: TtsRequestRepository
) : ViewModel() {

    private val _filterOptionsFlow = MutableStateFlow(FilterOptions())

    val voiceModelUiStateFlow = combine(
        voiceModelsRepository.getVoiceModels(),
        categoriesRepository.getCategories(),
        _filterOptionsFlow
    ) { voiceModels, categories, filterOptions ->
        VoiceModelUiState(
            voiceModels = voiceModels
                .filter {
                    filterOptions.filterByLanguage?.let {
                            filterByLanguage -> filterByLanguage == it.ietfPrimaryLanguageSubtag
                    } ?: true
                }
                .filter {
                    if (filterOptions.filterByChildCategory != null)
                        it.categoryTokens.contains(filterOptions.filterByChildCategory.categoryToken)
                    else if (filterOptions.filterByParentCategory != null)
                        it.categoryTokens.any { childCategoryToken ->
                            filterOptions.filterByParentCategory?.childrenCategories
                                ?.map { it.categoryToken }
                                ?.contains(childCategoryToken) ?: false
                        }
                    else
                        true
                },
            categories = categories,
            languages = voiceModels.map { it.ietfPrimaryLanguageSubtag }
        )
    }
        .asResult()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Result.Loading
        )

    fun filterBy(
        selectedLanguage: String? = null,
        selectedParentCategoryCompat: ParentCategoryCompat? = null,
        selectedChildCategoryCompact: ChildCategoryCompact? = null
    ) {
        _filterOptionsFlow.value = FilterOptions(
            selectedLanguage,
            selectedParentCategoryCompat,
            selectedChildCategoryCompact
        )
    }

    fun makeTtsRequest(voiceModelToken: String, text: String) {
        ttsRequestRepository.postTtsRequest(voiceModelToken, text)
    }

    data class VoiceModelUiState(
        val voiceModels: List<VoiceModelCompact>,
        val languages: List<String>,
        val categories: List<ParentCategoryCompat>,
    )

    data class FilterOptions(
        val filterByLanguage: String? = null,
        val filterByParentCategory: ParentCategoryCompat? = null,
        val filterByChildCategory: ChildCategoryCompact? = null
    )
}