package com.joseg.fakeyouclient.di

import com.joseg.fakeyouclient.BuildConfig
import com.joseg.fakeyouclient.network.service.FakeYouApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideFakeYouApi(): FakeYouApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.FAKEYOU_API_URL)
            .build()
            .create(FakeYouApi::class.java)
    }
}