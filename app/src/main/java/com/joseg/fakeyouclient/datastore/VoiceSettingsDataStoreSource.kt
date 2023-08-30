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
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class VoiceSettingsDataStoreSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val moshi: Moshi
) {
    suspend fun saveVoiceModel(voiceModel: VoiceModel) {
        try {
            dataStore.edit { preferences ->
                val jsonAdapter: JsonAdapter<VoiceModel> = moshi.adapter(VoiceModel::class.java)
                preferences[PreferencesKey.VOICE_MODEL] = jsonAdapter.toJson(voiceModel)
            }
        } catch (e: IOException) {
            Log.e("VoiceSettingsDataStoreSource", "Failed to edit voice settings preferences", e)
        }
    }

    fun getVoiceModel(): Flow<VoiceModel?> = dataStore.data.catch { exception ->
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
}