package com.joseg.fakeyouclient.ui.feature

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
class HomeViewModel @Inject constructor(
    private val voiceModelsRepository: VoiceModelsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val ttsRequestRepository: TtsRequestRepository
) : ViewModel() {

    private val _queryVoiceModelFlow = MutableStateFlow<QueryVoiceModel>(QueryVoiceModel())

    val voiceModelUiStateFlow = combine(
        voiceModelsRepository.getVoiceModels(),
        categoriesRepository.getCategories(),
        _queryVoiceModelFlow
    ) { voiceModels, categories, queryVoiceModel ->
        VoiceModelUiState(
            voiceModels = voiceModels.filter { voiceModel ->
                if (queryVoiceModel.filterByChildCategory != null)
                    voiceModel.categoryTokens.contains(queryVoiceModel.filterByChildCategory.categoryToken)
                else
                    voiceModel.categoryTokens.any { childCategoryToken ->
                        queryVoiceModel.filterByParentCategory?.childrenCategories
                            ?.map { it.categoryToken }
                            ?.contains(childCategoryToken) ?: false
                    }
            },
            categories = categories,
            selectedVoiceModel = voiceModels.random(),
            selectedParentCategory = queryVoiceModel.filterByParentCategory,
            selectedChildCategoryCompact = queryVoiceModel.filterByChildCategory
        )
    }
        .asResult()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Result.Loading
        )

    fun categoryToFilterBy(
        selectedParentCategoryCompat: ParentCategoryCompat,
        selectedChildCategoryCompact: ChildCategoryCompact? = null
    ) {
        _queryVoiceModelFlow.value = QueryVoiceModel(
            selectedParentCategoryCompat,
            selectedChildCategoryCompact
        )
    }

    fun makeTtsRequest(voiceModelToken: String, text: String) {
        ttsRequestRepository.postTtsRequest(voiceModelToken, text)
    }

    data class VoiceModelUiState(
        val voiceModels: List<VoiceModelCompact>,
        val categories: List<ParentCategoryCompat>,
        val selectedVoiceModel: VoiceModelCompact,
        val selectedParentCategory: ParentCategoryCompat?,
        val selectedChildCategoryCompact: ChildCategoryCompact?
    )

    data class QueryVoiceModel(
        val filterByParentCategory: ParentCategoryCompat? = null,
        val filterByChildCategory: ChildCategoryCompact? = null
    )
}