package com.joseg.fakeyouclient.di

import android.content.Context
import androidx.room.Room
import com.joseg.fakeyouclient.database.AppDataBase
import com.joseg.fakeyouclient.database.dao.AudioDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideFakeYouDataBase(@ApplicationContext context: Context): AppDataBase = Room.databaseBuilder(
        context,
        AppDataBase::class.java,
        "app-database"
    ).build()

    @Provides
    fun provideAudioFileDao(appDataBase: AppDataBase): AudioDao = appDataBase.audioDao()
}