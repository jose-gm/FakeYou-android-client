package com.joseg.fakeyouclient.data.cache

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryCache {
    private val hashMap = hashMapOf<String, Any>()
    private val mutex = Mutex()

    suspend fun put(key: String, data: Any) {
        mutex.withLock {
            hashMap[key] = data
        }
    }

    suspend fun get(key: String): Any? {
        mutex.withLock {
            return hashMap[key]
        }
    }

    fun clear(key: String) {
        hashMap.remove(key)
    }

    fun clearAll() {
        hashMap.clear()
    }
}

inline fun <reified T: Any> InMemoryCache.createCacheFlow(
    key: String,
    refreshCache: Boolean,
    crossinline source: suspend () -> T
) = flow {
    val data = get(key)
    if (!refreshCache && data != null)
        emit(data as T)
    else {
        clear(key)
        val result = source()
        put(key, result)
        emit(result)
    }
}