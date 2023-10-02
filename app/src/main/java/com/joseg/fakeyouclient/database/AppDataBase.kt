package com.joseg.fakeyouclient.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.joseg.fakeyouclient.database.dao.AudioDao
import com.joseg.fakeyouclient.database.entity.AudioEntity
import com.joseg.fakeyouclient.database.typeconverter.IntArrayConverter

@Database(entities = [AudioEntity::class], version = 2)
@TypeConverters(IntArrayConverter::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun audioDao(): AudioDao
}