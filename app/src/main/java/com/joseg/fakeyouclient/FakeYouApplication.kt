package com.joseg.fakeyouclient

import android.app.Application
import com.joseg.fakeyouclient.data.repository.CategoriesRepository
import com.joseg.fakeyouclient.data.repository.VoiceModelsRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FakeYouApplication : Application() {

    @Inject
    lateinit var voiceModelRepository: VoiceModelsRepository
    @Inject
    lateinit var categoryRepository: CategoriesRepository

    override fun onCreate() {
        super.onCreate()

        voiceModelRepository.getVoiceModels(true)
        categoryRepository.getCategories(true)
    }
}