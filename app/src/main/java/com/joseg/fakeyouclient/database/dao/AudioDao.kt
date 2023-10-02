package com.joseg.fakeyouclient.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.joseg.fakeyouclient.database.entity.AudioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioDao {
    @Insert
    suspend fun insert(audioEntity: AudioEntity)
    @Update
    suspend fun update(audioEntity: AudioEntity)
    @Delete
    suspend fun delete(audiosEntity: List<AudioEntity>)
    @Query("Delete FROM Audio")
    suspend fun deleteAll()
    @Query("SELECT * FROM Audio")
    fun getAll(): Flow<List<AudioEntity>>
}