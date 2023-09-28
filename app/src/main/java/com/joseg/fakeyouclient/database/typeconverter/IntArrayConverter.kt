package com.joseg.fakeyouclient.database.typeconverter

import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import javax.inject.Inject

class IntArrayConverter {
    @TypeConverter
    fun fromIntArray(intArray: IntArray): String {
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val jsonAdapter: JsonAdapter<IntArray> = moshi.adapter(IntArray::class.java)
        return jsonAdapter.toJson(intArray)
    }

    @TypeConverter
    fun toIntArray(json: String): IntArray {
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val jsonAdapter: JsonAdapter<IntArray> = moshi.adapter(IntArray::class.java)
        return try {
            jsonAdapter.fromJson(json) ?: intArrayOf()
        } catch (e: Throwable) {
            intArrayOf()
        }
    }
}