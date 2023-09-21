package com.joseg.fakeyouclient.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.joseg.fakeyouclient.common.Constants
import com.joseg.fakeyouclient.data.ApiResult
import com.joseg.fakeyouclient.data.cache.MemoryCache
import com.joseg.fakeyouclient.data.repository.implementation.BaseVoiceModelRepository
import com.joseg.fakeyouclient.data.fake.datasource.FakeYouRemoteDataSource
import com.joseg.fakeyouclient.data.testdouble.model.VoiceModelDummies
import com.joseg.fakeyouclient.datastore.VoiceModelPreferencesDataSource
import com.joseg.fakeyouclient.network.model.NetworkUserRatings
import com.joseg.fakeyouclient.network.model.NetworkVoiceModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class BaseVoiceModelRepositoryTest {
    private val unconfinedTestDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(unconfinedTestDispatcher + Job())
    private lateinit var memoryCache: MemoryCache
    private lateinit var voiceModelPreferencesDataSource: VoiceModelPreferencesDataSource
    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()
    private val dummyNetworkVoiceModel = listOf(
        NetworkVoiceModel(
            model_token = "TM:bysebgf36tkg",
            tts_model_type = "tacotron2",
            creator_user_token = "U:7S161Q96MG530",
            creator_username = "zombie",
            creator_display_name = "zombie",
            creator_gravatar_hash = "c9f26e22a4d10bb1b75e4d8a84c85660",
            title = "Arthur C. Clarke (901ep)",
            ietf_language_tag = "en-US",
            ietf_primary_language_subtag = "en",
            is_front_page_featured = false,
            is_twitch_featured = false,
            maybe_suggested_unique_bot_command = null,
            creator_set_visibility = "public",
            user_ratings = NetworkUserRatings(
                positive_count = 157,
                negative_count = 112,
                total_count = 269
            ),
            category_tokens = listOf(
                "CAT:46m8yaq2ceg",
                "CAT:gty64wem67f",
                "CAT:jhskand3g24"
            ),
            created_at = "2022-04-15T08:34:03Z",
            updated_at = "2023-07-30T13:19:47Z"
        )
    )

    @Before
    fun setUp() {
        memoryCache = MemoryCache()
        voiceModelPreferencesDataSource = VoiceModelPreferencesDataSource(
            PreferenceDataStoreFactory.create(
                scope = testScope,
                produceFile = { tmpFolder.newFile("test2.preferences_pb") }
            ),
            Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
        )
    }

    @Test
    fun `get voice models from source`() = testScope.runTest {
        val baseVoiceModelRepository = BaseVoiceModelRepository(
            FakeYouRemoteDataSource(),
            voiceModelPreferencesDataSource,
            memoryCache,
            unconfinedTestDispatcher
        )
        val cache = memoryCache.get(Constants.VOICE_MODELS_CACHE_KEY)
        assertEquals(cache, null)

        val voiceModelsFlow = baseVoiceModelRepository.getVoiceModels(true)

        val response = voiceModelsFlow.last() as ApiResult.Success

        assertEquals(response.data.first(), VoiceModelDummies.dummy1)
        assertEquals((memoryCache.get(Constants.VOICE_MODELS_CACHE_KEY) as List<NetworkVoiceModel>).first(), dummyNetworkVoiceModel.first())
    }

    @Test
    fun `get voice models from cache`() = testScope.runTest {
        val baseVoiceModelRepository = BaseVoiceModelRepository(
            FakeYouRemoteDataSource(),
            voiceModelPreferencesDataSource,
            memoryCache,
            unconfinedTestDispatcher
        )
        memoryCache.put(Constants.VOICE_MODELS_CACHE_KEY, dummyNetworkVoiceModel)
        assertEquals((memoryCache.get(Constants.VOICE_MODELS_CACHE_KEY) as List<NetworkVoiceModel>).first(), dummyNetworkVoiceModel.first())

        val voiceModelsFlow = baseVoiceModelRepository.getVoiceModels(false)
        val response = voiceModelsFlow.last() as ApiResult.Success

        assertEquals(response.data.first(), VoiceModelDummies.dummy1)
    }

//    @Test
//    fun saveVoiceModel() = testScope.runTest {
//        baseVoiceModelRepository.saveVoiceModel(dummyVoiceModel)
//        assertEquals(dummyVoiceModel, baseVoiceModelRepository.getVoiceModelSync())
//    }
}