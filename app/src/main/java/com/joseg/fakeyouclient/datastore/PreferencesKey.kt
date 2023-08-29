package com.joseg.fakeyouclient.datastore

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKey {
    val VOICE_MODEL = stringPreferencesKey("voice_model")
    val PENDING_VOICE_REQUESTS = stringPreferencesKey("pending_voice_requests")
}