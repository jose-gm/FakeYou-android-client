package com.joseg.fakeyouclient.datastore.cache.notification

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.joseg.fakeyouclient.datastore.PreferencesKey
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class NotificationStateCache @Inject constructor(
    private val datastore: DataStore<Preferences>,
    private val moshi: Moshi
) {
    private val mutex = Mutex()

    fun addOrUpdate(notificationState: NotificationState) = runBlocking {
        try {
            mutex.withLock {
                datastore.edit { preferences ->
                    val type = Types.newParameterizedType(List::class.java, NotificationState::class.java)
                    val json: JsonAdapter<List<NotificationState>> = moshi.adapter(type)
                    var list = preferences[PreferencesKey.TTS_REQUEST_NOTIFICATION_STATE]?.let {
                        json.fromJson(it)
                    } ?: emptyList()
                    list = list.toMutableList()
                    list.removeIf { it.notificationId == notificationState.notificationId }
                    list.add(notificationState)
                    preferences[PreferencesKey.TTS_REQUEST_NOTIFICATION_STATE] = json.toJson(list)
                }
            }
        } catch (e: Throwable) {
            Log.d("notificationState", e.stackTraceToString())
        }
    }

    fun remove(id: String) = runBlocking {
        try {
            mutex.withLock {
                datastore.edit { preferences ->
                    val type = Types.newParameterizedType(List::class.java, NotificationState::class.java)
                    val json: JsonAdapter<List<NotificationState>> = moshi.adapter(type)
                    var list = preferences[PreferencesKey.TTS_REQUEST_NOTIFICATION_STATE]?.let {
                        json.fromJson(it)
                    } ?: emptyList()
                    list = list.toMutableList()
                    list.removeIf { it.notificationId == id }
                    if (list.isNotEmpty())
                        preferences[PreferencesKey.TTS_REQUEST_NOTIFICATION_STATE] = json.toJson(list)
                    else
                        preferences.remove(PreferencesKey.TTS_REQUEST_NOTIFICATION_STATE)
                }
            }
        } catch (e: Throwable) {
            Log.d("notificationState", e.stackTraceToString())
        }
    }

    fun removeAll() = runBlocking {
        try {
            mutex.withLock {
                datastore.edit { preferences ->
                    preferences.remove(PreferencesKey.TTS_REQUEST_NOTIFICATION_STATE)
                }
            }
        } catch (e: Throwable) {
            Log.d("notificationState", e.stackTraceToString())
        }
    }

    fun getAll(): List<NotificationState> = runBlocking {
        try {
            datastore.data.map { preferences ->
                val type = Types.newParameterizedType(List::class.java, NotificationState::class.java)
                val json: JsonAdapter<List<NotificationState>> = moshi.adapter(type)
                preferences[PreferencesKey.TTS_REQUEST_NOTIFICATION_STATE]?.let {
                    json.fromJson(it)
                } ?: emptyList()
            }.first()
        } catch (e: Throwable) {
            Log.d("notificationState", e.stackTraceToString())
            return@runBlocking emptyList<NotificationState>()
        }
    }
}