package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.datastore.VoiceSettingsDataStoreSource
import com.joseg.fakeyouclient.model.VoiceModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VoiceSettingsRepository @Inject constructor(
    private val dataStoreSource: VoiceSettingsDataStoreSource
) {
     fun saveVoiceModel(voiceModel: VoiceModel) = dataStoreSource.saveVoiceModel(voiceModel)
    fun getVoiceModelFlow(): Flow<VoiceModel?> = dataStoreSource.getVoiceModelFlow()
    fun getVoiceModelSync(): VoiceModel? = dataStoreSource.getVoiceModelSync()
}