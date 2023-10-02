package com.joseg.fakeyouclient.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.joseg.fakeyouclient.model.VoiceModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.io.IOException
import javax.inject.Inject

class VoiceModelPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val moshi: Moshi
) {
     fun saveVoiceModel(voiceModel: VoiceModel) {
        runBlocking {
            try {
                dataStore.edit { preferences ->
                    val jsonAdapter: JsonAdapter<VoiceModel> = moshi.adapter(VoiceModel::class.java)
                    preferences[PreferencesKey.VOICE_MODEL] = jsonAdapter.toJson(voiceModel)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun getVoiceModelFlow(): Flow<VoiceModel?> = dataStore.data.catch { exception ->
        if (exception is IOException)
            emit(emptyPreferences())
        else
            throw exception
    }.map { preferences ->
        val jsonAdapter: JsonAdapter<VoiceModel> = moshi.adapter(VoiceModel::class.java)
        preferences[PreferencesKey.VOICE_MODEL]?.let {
            jsonAdapter.fromJson(it)
        }
    }

    fun getVoiceModelSync(): VoiceModel? = runBlocking {
        try {
            dataStore.data.map { preferences ->
                val jsonAdapter: JsonAdapter<VoiceModel> = moshi.adapter(VoiceModel::class.java)
                preferences[PreferencesKey.VOICE_MODEL]?.let {
                    jsonAdapter.fromJson(it)
                }
            }.first()
        } catch (e: Exception) {
            Log.e("voiceSettings", e.stackTraceToString())
            null
        }
    }
}