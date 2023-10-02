package com.joseg.fakeyouclient.common.enums

import androidx.annotation.DrawableRes
import com.joseg.fakeyouclient.R

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

enum class LanguageTag {
    EN,
    ES,
    DE,
    FR,
    IT,
    PT,
    TR,
    AR;

    companion object {
        fun parse(languageTag: String): LanguageTag = when (languageTag.lowercase()) {
            "en" -> EN
            "es" -> ES
            "de" -> DE
            "fr" -> FR
            "it" -> IT
            "pt" -> PT
            "tr" -> TR
            else -> AR
        }
    }
}

@DrawableRes
fun LanguageTag.getFlagIconRes(): Int = when (this) {
    LanguageTag.EN -> R.drawable.ic_flag_us
    LanguageTag.ES -> R.drawable.ic_flag_es
    LanguageTag.DE -> R.drawable.ic_flag_de
    LanguageTag.FR -> R.drawable.ic_flag_fr
    LanguageTag.IT -> R.drawable.ic_flag_it
    LanguageTag.PT -> R.drawable.ic_flag_br
    LanguageTag.TR -> R.drawable.ic_flag_tr
    else -> R.drawable.ic_flag_ae
}

enum class FilterOptions {
    LANGUAGE,
    CATEGORY,
    SUB_CATEGORY;

    companion object {
        fun parse(menuOption: String): FilterOptions = when (menuOption.lowercase()) {
            "language" -> LANGUAGE
            "category" -> CATEGORY
            else -> SUB_CATEGORY
        }
    }
}