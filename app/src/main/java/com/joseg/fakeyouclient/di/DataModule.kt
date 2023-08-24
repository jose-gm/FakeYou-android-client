package com.joseg.fakeyouclient.di

import com.joseg.fakeyouclient.data.cache.MemoryCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideInMemoryCache(): MemoryCache = MemoryCache()
}