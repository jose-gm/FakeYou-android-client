package com.joseg.fakeyouclient.domain

import com.joseg.fakeyouclient.data.repository.AudioRepository
import com.joseg.fakeyouclient.model.Audio
import javax.inject.Inject

class InsertAudioUseCase @Inject constructor(
    private val audioRepository: AudioRepository
) {
    suspend operator fun invoke(audio: Audio) = audioRepository.insert(audio)
}