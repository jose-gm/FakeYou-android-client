package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.common.Constants
import com.joseg.fakeyouclient.data.ApiResult
import com.joseg.fakeyouclient.data.cache.MemoryCache
import com.joseg.fakeyouclient.data.repository.implementation.BaseCategoryRepository
import com.joseg.fakeyouclient.data.fake.datasource.FakeYouEmptyRemoteDataSource
import com.joseg.fakeyouclient.data.fake.datasource.FakeYouRemoteDataSource
import com.joseg.fakeyouclient.model.Category
import com.joseg.fakeyouclient.network.model.NetworkCategory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BaseCategoryRepositoryTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val unconfinedDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(unconfinedDispatcher)
    private lateinit var memoryCache: MemoryCache
    private val dummyNetworkCategories = listOf(
        NetworkCategory(
            category_token = "CAT:akt2tam28ja",
            model_type = "tts",
            maybe_super_category_token = null,
            can_directly_have_models = false,
            can_have_subcategories = true,
            can_only_mods_apply = true,
            name = "Video Games",
            name_for_dropdown = "Games",
            is_mod_approved = true,
            is_synthetic = false,
            should_be_sorted = true,
            created_at = "2022-07-14T15:41:05Z",
            updated_at = "2022-07-14T15:42:10Z"
        )
    )
    private val dummyCategory = Category(
        categoryToken = "CAT:akt2tam28ja",
        modelType = "tts",
        maybeSuperCategoryToken = null,
        canDirectlyHaveModels = false,
        canHaveSubcategories = true,
        canOnlyModsApply = true,
        name = "Video Games",
        nameForDropdown = "Games",
        isModApproved = true,
        isSynthetic = false,
        shouldBeSorted = true,
        createdAt = "2022-07-14T15:41:05Z",
        updatedAt = "2022-07-14T15:42:10Z",
        subCategories = null
    )

    @Before
    fun setUp() {
        memoryCache = MemoryCache()
    }

    @Test
    fun `get categories from source`() = testScope.runTest {
        val baseCategoryRepository = BaseCategoryRepository(FakeYouRemoteDataSource(), memoryCache, unconfinedDispatcher)
        val categoriesFlow = baseCategoryRepository.getCategories(true)
        assertEquals(memoryCache.get(Constants.CATEGORIES_MODELS_CACHE_KEY), null)

        val response = categoriesFlow.first() as ApiResult.Success
        assertEquals(response.data.first().copy(subCategories = null), dummyCategory)
    }

    @Test
    fun `get categories from cache`() = testScope.runTest {
        val baseCategoryRepository = BaseCategoryRepository(FakeYouEmptyRemoteDataSource(), memoryCache, unconfinedDispatcher)
        memoryCache.put(Constants.CATEGORIES_MODELS_CACHE_KEY, dummyNetworkCategories)
        assertEquals((memoryCache.get(Constants.CATEGORIES_MODELS_CACHE_KEY) as List<NetworkCategory>).first(), dummyNetworkCategories.first())

        val categoriesFlow = baseCategoryRepository.getCategories(false)
        val response = categoriesFlow.first() as ApiResult.Success
        assertEquals(response.data.first().copy(subCategories = null), dummyCategory)
    }
}