package com.joseg.fakeyouclient.domain

import com.joseg.fakeyouclient.data.repository.AudioRepository
import com.joseg.fakeyouclient.model.Audio
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAudiosUseCase @Inject constructor(
    private val audioRepository: AudioRepository
) {
    operator fun invoke(): Flow<List<Audio>> = audioRepository.getAll()
}