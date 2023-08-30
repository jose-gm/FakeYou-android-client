package com.joseg.fakeyouclient.ui.feature.textToSpeech

import androidx.lifecycle.ViewModel
import com.joseg.fakeyouclient.data.repository.CategoriesRepository
import com.joseg.fakeyouclient.data.repository.TtsRequestRepository
import com.joseg.fakeyouclient.data.repository.VoiceModelsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TextToSpeechViewModel @Inject constructor(
    private val voiceModelsRepository: VoiceModelsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val ttsRequestRepository: TtsRequestRepository
) : ViewModel() {

//    private val _filterOptionsFlow = MutableStateFlow(FilterOptions())
//
//    val voiceModelUiStateFlow = combine(
//        voiceModelsRepository.getVoiceModels(),
//        categoriesRepository.getCategories(),
//        _filterOptionsFlow
//    ) { voiceModels, categories, filterOptions ->
//        VoiceModelUiState(
//            voiceModels = voiceModels
//                .filter {
//                    filterOptions.filterByLanguage?.let {
//                            filterByLanguage -> filterByLanguage == it.ietfPrimaryLanguageSubtag
//                    } ?: true
//                }
//                .filter {
//                    if (filterOptions.filterByChildCategory != null)
//                        it.categoryTokens.contains(filterOptions.filterByChildCategory.categoryToken)
//                    else if (filterOptions.filterByParentCategoryCompact != null)
//                        it.categoryTokens.any { childCategoryToken ->
//                            filterOptions.filterByParentCategoryCompact?.childrenCategories
//                                ?.map { it.categoryToken }
//                                ?.contains(childCategoryToken) ?: false
//                        }
//                    else
//                        true
//                },
//            categories = categories,
//            languages = voiceModels.map { it.ietfPrimaryLanguageSubtag }
//        )
//    }
//        .asResult()
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(),
//            initialValue = Result.Loading
//        )
//
//    fun filterBy(
//        selectedLanguage: LanguageTag? = null,
//        selectedCategoryCompact: CategoryCompact? = null,
//        selectedChildCategoryCompact: ChildCategoryCompact? = null
//    ) {
//        _filterOptionsFlow.value = FilterOptions(
//            selectedLanguage,
//            selectedCategoryCompact,
//            selectedChildCategoryCompact
//        )
//    }
//
//    fun makeTtsRequest(voiceModelToken: String, text: String) {
//        ttsRequestRepository.postTtsRequest(voiceModelToken, text)
//    }
//
//    data class VoiceModelUiState(
//        val voiceModels: List<VoiceModelCompact>,
//        val languages: List<LanguageTag>,
//        val categories: List<CategoryCompact>,
//    )
//
//    data class FilterOptions(
//        val filterByLanguage: LanguageTag? = null,
//        val filterByParentCategoryCompact: CategoryCompact? = null,
//        val filterByChildCategory: ChildCategoryCompact? = null
//    )
}