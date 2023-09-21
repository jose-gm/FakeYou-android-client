package com.joseg.fakeyouclient.ui

import com.joseg.fakeyouclient.common.enums.FilterOptions
import com.joseg.fakeyouclient.common.enums.LanguageTag
import com.joseg.fakeyouclient.data.fake.repository.FakeCategoryRepository
import com.joseg.fakeyouclient.data.fake.repository.FakeVoiceModelRepository
import com.joseg.fakeyouclient.data.testdouble.model.CategoryDummies
import com.joseg.fakeyouclient.data.testdouble.model.VoiceModelDummies
import com.joseg.fakeyouclient.domain.GetVoiceModelUseCase
import com.joseg.fakeyouclient.domain.SaveVoiceModelUseCase
import com.joseg.fakeyouclient.model.Category
import com.joseg.fakeyouclient.rule.MainDispatcherRule
import com.joseg.fakeyouclient.ui.feature.voiceSelection.VoiceSelectionViewModel
import com.joseg.fakeyouclient.ui.shared.UiState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class VoiceSelectionViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var voiceSelectionViewModel: VoiceSelectionViewModel

    @Before
    fun setUp() {
        val voiceModelRepository = FakeVoiceModelRepository()
        voiceSelectionViewModel = VoiceSelectionViewModel(
            GetVoiceModelUseCase(voiceModelRepository),
            SaveVoiceModelUseCase(voiceModelRepository),
            FakeCategoryRepository()
        )
    }

    @Test
    fun `voiceModelUiState is loading initially`() = runTest {
        assertEquals(voiceSelectionViewModel.voiceModelUiStateFlow.value, UiState.Loading)
    }

    @Test
    fun `filterUiState is loading initially`() = runTest {
        assertEquals(voiceSelectionViewModel.filterUiStateStateFlow.value, UiState.Loading)
    }

    @Test
    fun getVoiceModelUiStateWhenLoaded() = runTest {
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { voiceSelectionViewModel.voiceModelUiStateFlow.collect() }
        val uiState = (voiceSelectionViewModel.voiceModelUiStateFlow.value as UiState.Success).data

        assertEquals(uiState.voiceModels.isNotEmpty(), true)
        job.cancel()
    }

    @Test
    fun getLanguageFiltersWhenLoaded() = runTest {
        voiceSelectionViewModel.submitFilterSelection(FilterOptions.LANGUAGE)
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { voiceSelectionViewModel.filterUiStateStateFlow.collect() }
        val uiState = (voiceSelectionViewModel.filterUiStateStateFlow.value as UiState.Success).data

        assertEquals(uiState.selectedFilter, FilterOptions.LANGUAGE)
        assertEquals(uiState.checkItems.isNotEmpty(), true)
        job.cancel()
    }

    @Test
    fun setSelectedLanguageCheckItem() = runTest {
        voiceSelectionViewModel.submitFilterSelection(FilterOptions.LANGUAGE)
        voiceSelectionViewModel.submitCheckItemData(VoiceModelDummies.dummyList.map { it.ietfPrimaryLanguageSubtag }.toSet().first())
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { voiceSelectionViewModel.filterUiStateStateFlow.collect() }
        val uiState = (voiceSelectionViewModel.filterUiStateStateFlow.value as UiState.Success).data

        assertEquals(uiState.selectedFilter, FilterOptions.LANGUAGE)
        assertEquals(uiState.checkItems.find { it.isSelected }?.data as LanguageTag, LanguageTag.EN)
        job.cancel()
    }

    @Test
    fun getCategoryFiltersWhenLoaded() = runTest {
        voiceSelectionViewModel.submitFilterSelection(FilterOptions.CATEGORY)
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { voiceSelectionViewModel.filterUiStateStateFlow.collect() }
        val uiState = (voiceSelectionViewModel.filterUiStateStateFlow.value as UiState.Success).data

        assertEquals(uiState.selectedFilter, FilterOptions.CATEGORY)
        assertEquals(uiState.checkItems.isNotEmpty(), true)
        job.cancel()
    }

    @Test
    fun setSelectedCategoryCheckItem() = runTest {
        voiceSelectionViewModel.submitFilterSelection(FilterOptions.CATEGORY)
        voiceSelectionViewModel.submitCheckItemData(CategoryDummies.dummy1)
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { voiceSelectionViewModel.filterUiStateStateFlow.collect() }
        val uiState = (voiceSelectionViewModel.filterUiStateStateFlow.value as UiState.Success).data

        assertEquals(uiState.selectedFilter, FilterOptions.CATEGORY)
        assertEquals(uiState.checkItems.find { it.isSelected }?.data as Category, CategoryDummies.dummy1)
        job.cancel()
    }

    @Test
    fun getSubCategoryFromSelectedCategoryFiltersWhenLoaded() = runTest {
        voiceSelectionViewModel.submitFilterSelection(FilterOptions.CATEGORY)
        voiceSelectionViewModel.submitCheckItemData(CategoryDummies.dummyWithSubCategory1)
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { voiceSelectionViewModel.filterUiStateStateFlow.collect() }
        val uiState = (voiceSelectionViewModel.filterUiStateStateFlow.value as UiState.Success).data

        assertEquals(uiState.selectedFilter, FilterOptions.CATEGORY)
        assertEquals(uiState.subCategoryLabel, CategoryDummies.dummyWithSubCategory1.nameForDropdown)
        job.cancel()
    }

    @Test
    fun getSubCategoryFiltersWhenLoaded() = runTest {
        voiceSelectionViewModel.submitFilterSelection(FilterOptions.CATEGORY)
        voiceSelectionViewModel.submitCheckItemData(CategoryDummies.dummyWithSubCategory1)
        voiceSelectionViewModel.submitFilterSelection(FilterOptions.SUB_CATEGORY)
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { voiceSelectionViewModel.filterUiStateStateFlow.collect() }
        val uiState = (voiceSelectionViewModel.filterUiStateStateFlow.value as UiState.Success).data

        assertEquals(uiState.selectedFilter, FilterOptions.SUB_CATEGORY)
        assertEquals(uiState.checkItems.isNotEmpty(), true)
        job.cancel()
    }

    @Test
    fun setSelectedSubCategoryCheckItem() = runTest {
        voiceSelectionViewModel.submitFilterSelection(FilterOptions.CATEGORY)
        voiceSelectionViewModel.submitCheckItemData(CategoryDummies.dummyWithSubCategory1)
        voiceSelectionViewModel.submitFilterSelection(FilterOptions.SUB_CATEGORY)
        voiceSelectionViewModel.submitCheckItemData(CategoryDummies.dummyWithSubCategory1.subCategories!!.first())
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { voiceSelectionViewModel.filterUiStateStateFlow.collect() }
        val uiState = (voiceSelectionViewModel.filterUiStateStateFlow.value as UiState.Success).data

        assertEquals(uiState.selectedFilter, FilterOptions.SUB_CATEGORY)
        assertEquals(uiState.checkItems.find { it.isSelected }?.data as Category, CategoryDummies.dummyWithSubCategory1.subCategories!!.first())
        job.cancel()
    }

    @Test
    fun filterVoiceModelUiStateByLanguage() = runTest {
        voiceSelectionViewModel.submitCheckItemData(VoiceModelDummies.dummyList.map { it.ietfPrimaryLanguageSubtag }.toSet().first())
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { voiceSelectionViewModel.voiceModelUiStateFlow.collect() }
        val uiState = (voiceSelectionViewModel.voiceModelUiStateFlow.value as UiState.Success).data

        assertEquals(uiState.voiceModels.all { it.ietfPrimaryLanguageSubtag == LanguageTag.EN }, true)
        assertEquals(uiState.isFilterActive, true)
        job.cancel()
    }

    @Test
    fun filterVoiceModelUiStateByCategory() = runTest {
        voiceSelectionViewModel.submitCheckItemData(CategoryDummies.dummyWithSubCategory1)
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { voiceSelectionViewModel.voiceModelUiStateFlow.collect() }
        val uiState = (voiceSelectionViewModel.voiceModelUiStateFlow.value as UiState.Success).data

        assertEquals(
            uiState.voiceModels.all {
                if (CategoryDummies.dummyWithSubCategory1.subCategories?.isEmpty() == true ||
                    CategoryDummies.dummyWithSubCategory1.subCategories == null) {
                    it.categoryTokens.contains(CategoryDummies.dummyWithSubCategory1.categoryToken)
                } else {
                    it.categoryTokens.any { token ->
                        CategoryDummies.dummyWithSubCategory1.subCategories
                            ?.map { subCategory -> subCategory.categoryToken }
                            ?.contains(token) ?: false
                    }
                }
            },
            true
        )
        assertEquals(uiState.isFilterActive, true)
        job.cancel()
    }

    @Test
    fun filterVoiceModelUiStateBySubCategory() = runTest {
        voiceSelectionViewModel.submitCheckItemData(CategoryDummies.dummyWithSubCategory1.subCategories!!.last())
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { voiceSelectionViewModel.voiceModelUiStateFlow.collect() }
        val uiState = (voiceSelectionViewModel.voiceModelUiStateFlow.value as UiState.Success).data

        assertEquals(
            uiState.voiceModels.all {
                it.categoryTokens.contains(CategoryDummies.dummyWithSubCategory1.subCategories!!.last().categoryToken)
            },
            true
        )
        assertEquals(uiState.isFilterActive, true)
        job.cancel()
    }

    @Test
    fun filterVoiceModelUiStateBySearchQuery() = runTest {
        val searchQuery = "Kong"
        voiceSelectionViewModel.submitSearchQuery(searchQuery)
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { voiceSelectionViewModel.voiceModelUiStateFlow.collect() }
        val uiState = (voiceSelectionViewModel.voiceModelUiStateFlow.value as UiState.Success).data

        assertEquals(
            uiState.voiceModels.all {
                it.title.contains(searchQuery)
            },
            true
        )
        job.cancel()
    }
}