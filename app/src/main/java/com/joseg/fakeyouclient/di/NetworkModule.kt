package com.joseg.fakeyouclient.di

import com.joseg.fakeyouclient.BuildConfig
import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import com.joseg.fakeyouclient.network.retrofit.FakeYouApi
import com.joseg.fakeyouclient.network.retrofit.RetrofitRemoteDataSource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

    @Provides
    fun provideRetrofit(moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.FAKEYOU_API_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    fun provideFakeYouApi(retrofit: Retrofit): FakeYouApi =
        retrofit
            .create(FakeYouApi::class.java)

    @Provides
    fun provideFakeYouRemoteDataSource(fakeYouApi: FakeYouApi): FakeYouRemoteDataSource =
        RetrofitRemoteDataSource(fakeYouApi)
}