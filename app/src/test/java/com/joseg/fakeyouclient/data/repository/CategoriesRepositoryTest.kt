package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.data.testdouble.TestFakeYouRemoteDataSource
import com.joseg.fakeyouclient.model.ChildCategoryCompact
import com.joseg.fakeyouclient.model.ParentCategoryCompat
import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class CategoriesRepositoryTest {

    private lateinit var categoriesRepository: CategoriesRepository
    private lateinit var remoteDataSource: FakeYouRemoteDataSource
    private lateinit var dispatcher: CoroutineDispatcher

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        dispatcher = UnconfinedTestDispatcher()
        remoteDataSource = TestFakeYouRemoteDataSource()
        categoriesRepository = CategoriesRepository(remoteDataSource, dispatcher)
    }

    @Test
    fun `successfully get categories from remoteSource`() = runTest {
        val categoriesFlow = categoriesRepository.getCategories()
        val dummyParentCategoryCompact = ParentCategoryCompat(
            categoryToken = "CAT:akt2tam28ja",
            modelType = "tts",
            nameForDropdown = "Games",
            childrenCategories = listOf(
                ChildCategoryCompact(
                    categoryToken = "CAT:xm6jbgyxk9s",
                    modelType = "tts",
                    maybeSuperCategoryToken = "CAT:akt2tam28ja",
                    nameForDropdown = " Bayonetta"
                ),
                ChildCategoryCompact(
                    categoryToken = "CAT:pa3bmz77jz5",
                    modelType = "tts",
                    maybeSuperCategoryToken = "CAT:akt2tam28ja",
                    nameForDropdown = "Friday Night Funkin’"
                ),
                ChildCategoryCompact(
                    categoryToken = "CAT:j4ty8xzdkmq",
                    modelType = "tts",
                    maybeSuperCategoryToken = "CAT:akt2tam28ja",
                    nameForDropdown = "Donkey Kong series"
                ),
                ChildCategoryCompact(
                    categoryToken = "CAT:yp4m7nz3fmn",
                    modelType = "tts",
                    maybeSuperCategoryToken = "CAT:akt2tam28ja",
                    nameForDropdown = "Danganronpa"
                )
            )
        )

        assertEquals(categoriesFlow.first().first(), dummyParentCategoryCompact)
    }
}