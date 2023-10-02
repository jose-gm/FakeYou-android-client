package com.joseg.fakeyouclient.datastore

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKey {
    val VOICE_MODEL = stringPreferencesKey("voice_model")
    val TTS_REQUEST_NOTIFICATION_STATE = stringPreferencesKey("tts_requests_notification_state")
}