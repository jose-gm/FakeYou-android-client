package com.joseg.fakeyouclient.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joseg.fakeyouclient.database.dao.AudioDao
import com.joseg.fakeyouclient.database.entity.AudioEntity

@Database(entities = [AudioEntity::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun audioDao(): AudioDao
}