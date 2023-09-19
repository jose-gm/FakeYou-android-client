package com.joseg.fakeyouclient.domain

import com.joseg.fakeyouclient.data.repository.VoiceModelRepository
import com.joseg.fakeyouclient.model.VoiceModel
import javax.inject.Inject

class SaveVoiceModelUseCase @Inject constructor(
    private val voiceModelRepository: VoiceModelRepository
) {
    operator fun invoke(voiceModel: VoiceModel) {
        voiceModelRepository.saveVoiceModel(voiceModel)
    }
}