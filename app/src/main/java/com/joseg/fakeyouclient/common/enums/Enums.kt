package com.joseg.fakeyouclient.common.enums

enum class TtsRequestStatusType {
    PENDING,
    STARTED,
    COMPLETE_SUCCESS,
    COMPLETE_FAILURE,
    ATTEMPT_FAILED,
    DEAD;

    companion object {
        fun parse(status: String): TtsRequestStatusType = when (status.lowercase()) {
            "pending" -> PENDING
            "started" -> STARTED
            "complete_success" -> COMPLETE_SUCCESS
            "complete_failure" -> COMPLETE_FAILURE
            "attempt_failed" -> ATTEMPT_FAILED
            else -> DEAD
        }
    }
}