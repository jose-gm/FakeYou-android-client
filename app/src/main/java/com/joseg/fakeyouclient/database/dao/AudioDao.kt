package com.joseg.fakeyouclient.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.joseg.fakeyouclient.database.entity.AudioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioDao {
    @Insert
    suspend fun insert(audioEntity: AudioEntity)
    @Delete
    suspend fun delete(audioEntity: AudioEntity)
    @Query("Delete FROM Audio")
    suspend fun deleteAll()
    @Query("SELECT * FROM Audio")
    fun getAll(): Flow<List<AudioEntity>>
}