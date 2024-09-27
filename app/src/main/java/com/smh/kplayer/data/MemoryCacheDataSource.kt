package com.smh.kplayer.data

const val HARDWARE_ACCELERATION = "HardwareAcceleration"

class MemoryCacheDataSource {
    private val cache = mutableMapOf<String, Any>()

    @Synchronized
    @Suppress("UNCHECKED_CAST")
    fun <T: Any> get(key: String): T? {
        return cache[key] as? T
    }

    @Synchronized
    fun <T: Any> set(key: String, value: T) {
        cache[key] = value
    }

    @Synchronized
    fun remove(key: String) {
        cache.remove(key)
    }

    @Synchronized
    fun clear() {
        cache.clear()
    }
}