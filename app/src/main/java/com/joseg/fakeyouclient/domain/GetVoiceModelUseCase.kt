package com.joseg.fakeyouclient.domain

import com.joseg.fakeyouclient.data.ApiResult
import com.joseg.fakeyouclient.data.repository.VoiceModelRepository
import com.joseg.fakeyouclient.model.VoiceModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVoiceModelUseCase @Inject constructor(
    private val voiceModelRepository: VoiceModelRepository
) {
    fun getVoiceModels(refresh: Boolean = false): Flow<ApiResult<List<VoiceModel>>> = voiceModelRepository.getVoiceModels(refresh)
    fun getVoiceModel(): VoiceModel? = voiceModelRepository.getVoiceModelSync()
}