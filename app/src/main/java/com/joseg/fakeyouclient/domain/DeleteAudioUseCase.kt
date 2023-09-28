package com.joseg.fakeyouclient.domain

import com.joseg.fakeyouclient.data.repository.AudioRepository
import com.joseg.fakeyouclient.model.Audio
import javax.inject.Inject

class DeleteAudioUseCase @Inject constructor(
    private val audioRepository: AudioRepository
) {
    suspend fun delete(audio: Audio) = audioRepository.delete(audio)
    suspend fun deleteAll() = audioRepository.deleteAll()
}