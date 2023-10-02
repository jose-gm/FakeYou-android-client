package com.joseg.fakeyouclient.data.model

import com.joseg.fakeyouclient.common.enums.TtsRequestStatusType
import com.joseg.fakeyouclient.model.TtsRequestState
import com.joseg.fakeyouclient.network.model.NetworkTtsRequestState

fun NetworkTtsRequestState.asTtsRequestState() = TtsRequestState(
    jobToken = job_token,
    status = TtsRequestStatusType.parse(status),
    maybeExtraStatusDescription = maybe_extra_status_description,
    attemptCount = attempt_count,
    maybeResultToken = maybe_result_token,
    maybePublicBucketWavAudioPath = maybe_public_bucket_wav_audio_path,
    modelToken = model_token,
    ttsModelType = tts_model_type,
    title = title,
    rawInferenceText = raw_inference_text,
    createdAt = created_at,
    updatedAt = updated_at
)