package com.joseg.fakeyouclient.data.repository

import com.joseg.fakeyouclient.data.ApiResult
import com.joseg.fakeyouclient.model.VoiceModel
import kotlinx.coroutines.flow.Flow

interface VoiceModelRepository {
    fun getVoiceModels(refresh: Boolean = false): Flow<ApiResult<List<VoiceModel>>>
    fun saveVoiceModel(voiceModel: VoiceModel)
    fun getVoiceModelSync(): VoiceModel?
}