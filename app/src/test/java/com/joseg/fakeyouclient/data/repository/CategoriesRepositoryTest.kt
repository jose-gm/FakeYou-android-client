package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.data.testdouble.TestFakeYouRemoteDataSource
import com.joseg.fakeyouclient.model.CategoryCompact
import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class CategoriesRepositoryTest {

    private lateinit var categoriesRepository: CategoriesRepository
    private lateinit var remoteDataSource: FakeYouRemoteDataSource

    @Before
    fun setUp() {
        remoteDataSource = TestFakeYouRemoteDataSource()
        categoriesRepository = CategoriesRepository(remoteDataSource)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `successfully get categories from remoteSource`() = runTest(UnconfinedTestDispatcher()) {
        val categoriesFlow = categoriesRepository.getCategories()
        val dummyCategory = CategoryCompact(
            categoryToken = "CAT:akt2tam28ja",
            modelType = "tts",
            maybeSuperCategoryToken = null,
            nameForDropdown = "Games",
            subCategories = listOf(
                CategoryCompact(
                    categoryToken = "CAT:xm6jbgyxk9s",
                    modelType = "tts",
                    maybeSuperCategoryToken = "CAT:akt2tam28ja",
                    nameForDropdown = " Bayonetta",
                    subCategories = null
                ),
                CategoryCompact(
                    categoryToken = "CAT:pa3bmz77jz5",
                    modelType = "tts",
                    maybeSuperCategoryToken = "CAT:akt2tam28ja",
                    nameForDropdown = "Friday Night Funkin’",
                    subCategories = null
                ),
                CategoryCompact(
                    categoryToken = "CAT:j4ty8xzdkmq",
                    modelType = "tts",
                    maybeSuperCategoryToken = "CAT:akt2tam28ja",
                    nameForDropdown = "Donkey Kong series",
                    subCategories = null
                ),
                CategoryCompact(
                    categoryToken = "CAT:yp4m7nz3fmn",
                    modelType = "tts",
                    maybeSuperCategoryToken = "CAT:akt2tam28ja",
                    nameForDropdown = "Danganronpa",
                    subCategories = null
                )
            )
        )

        assertEquals(categoriesFlow.first().first(), dummyCategory)
    }
}