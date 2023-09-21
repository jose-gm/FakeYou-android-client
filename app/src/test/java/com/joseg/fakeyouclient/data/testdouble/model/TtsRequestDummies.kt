package com.joseg.fakeyouclient.data.testdouble.model

import com.joseg.fakeyouclient.common.enums.TtsRequestStatusType
import com.joseg.fakeyouclient.model.TtsRequestState

object TtsRequestDummies {
    val dummyInferenceJobToken = "JTINF:qsy72wnfashhvnkktc16y49cy1"

    val dummyPendingTtsRequestState = TtsRequestState(
        jobToken = dummyInferenceJobToken,
        status = TtsRequestStatusType.PENDING,
        maybeExtraStatusDescription = null,
        attemptCount = 0,
        maybeResultToken = null,
        maybePublicBucketWavAudioPath = null,
        modelToken = "TM:7wbtjphx8h8v",
        ttsModelType = "tacotron2",
        title = "Mario*",
        rawInferenceText = "This is a use of the voice",
        createdAt = "2022-02-28T05:39:36Z",
        updatedAt = "2022-02-28T05:39:51Z"
    )

    val dummyStartedTtsRequestState = TtsRequestState(
        jobToken = dummyInferenceJobToken,
        status = TtsRequestStatusType.STARTED,
        maybeExtraStatusDescription = null,
        attemptCount = 0,
        maybeResultToken = null,
        maybePublicBucketWavAudioPath = null,
        modelToken = "TM:7wbtjphx8h8v",
        ttsModelType = "tacotron2",
        title = "Mario*",
        rawInferenceText = "This is a use of the voice",
        createdAt = "2022-02-28T05:39:36Z",
        updatedAt = "2022-02-28T05:39:51Z"
    )

    val dummyCompletedTtsRequestState = TtsRequestState(
        jobToken = dummyInferenceJobToken,
        status = TtsRequestStatusType.COMPLETE_SUCCESS,
        maybeExtraStatusDescription = "done",
        attemptCount = 0,
        maybeResultToken = "TR:tn7gq96wg6httvnq91y4y9fka76nj",
        maybePublicBucketWavAudioPath = "/tts_inference_output/9/c/d/vocodes_9cdd9865-0e10-48f0-9a23-861118ec3286.wav",
        modelToken = "TM:7wbtjphx8h8v",
        ttsModelType = "tacotron2",
        title = "Mario*",
        rawInferenceText = "This is a use of the voice",
        createdAt = "2022-02-28T05:39:36Z",
        updatedAt = "2022-02-28T05:39:51Z"
    )
}