package com.joseg.fakeyouclient.di

import android.content.Context
import com.joseg.fakeyouclient.BuildConfig
import com.joseg.fakeyouclient.network.FakeYouRemoteDataSource
import com.joseg.fakeyouclient.network.retrofit.FakeYouApi
import com.joseg.fakeyouclient.network.retrofit.RetrofitRemoteSource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import linc.com.amplituda.Amplituda
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
    fun provideFakeYouApi(moshi: Moshi): FakeYouApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.FAKEYOU_API_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(FakeYouApi::class.java)

    @Provides
    fun provideFakeYouRemoteDataSource(fakeYouApi: FakeYouApi): FakeYouRemoteDataSource =
        RetrofitRemoteSource(fakeYouApi)

    @Provides
    fun provideAmplituda(@ApplicationContext context: Context): Amplituda = Amplituda(context)
}