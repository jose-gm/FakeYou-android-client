package com.joseg.fakeyouclient.di

import android.content.Context
import com.joseg.fakeyouclient.data.cache.MemoryCache
import com.joseg.fakeyouclient.data.download.AndroidDownloader
import com.joseg.fakeyouclient.data.download.Downloader
import com.joseg.fakeyouclient.data.localDataSource.AudioDatabaseSource
import com.joseg.fakeyouclient.data.localDataSource.AudioLocalDataSource
import com.joseg.fakeyouclient.database.dao.AudioDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Singleton
    @Provides
    fun provideMemoryCache(): MemoryCache = MemoryCache()

    @Provides
    fun provideAndroidDownloader(@ApplicationContext context: Context, ioDispatcher: CoroutineDispatcher): Downloader = AndroidDownloader(context, ioDispatcher)

    @Provides
    fun provideAudioDatabaseSource(audioDao: AudioDao): AudioLocalDataSource = AudioDatabaseSource(audioDao)

    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}