package com.joseg.fakeyouclient.di

import com.joseg.fakeyouclient.data.repository.AudioRepository
import com.joseg.fakeyouclient.data.repository.CategoryRepository
import com.joseg.fakeyouclient.data.repository.TtsRequestRepository
import com.joseg.fakeyouclient.data.repository.VoiceModelRepository
import com.joseg.fakeyouclient.data.repository.implementation.BaseAudioRepository
import com.joseg.fakeyouclient.data.repository.implementation.BaseCategoryRepository
import com.joseg.fakeyouclient.data.repository.implementation.BaseTtsRequestRepository
import com.joseg.fakeyouclient.data.repository.implementation.BaseVoiceModelRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun bindVoiceModelRepository(baseVoiceModelRepository: BaseVoiceModelRepository): VoiceModelRepository
    @Binds
    fun bindCategoryRepository(baseCategoryRepository: BaseCategoryRepository): CategoryRepository
    @Binds
    fun bindTtsRequestRepository(baseTtsRequestRepository: BaseTtsRequestRepository): TtsRequestRepository
    @Binds
    fun bindAudioRepository(baseAudioRepository: BaseAudioRepository): AudioRepository
}