package com.smh.player.manager

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@UnstableApi
object CacheManager {
    private var cacheInstance: Cache? = null

    fun getCache(context: Context): Cache {
        if (cacheInstance == null) {
            val cacheDirectory = File(context.cacheDir, "media3")
            cacheInstance = SimpleCache(
                cacheDirectory,
                LeastRecentlyUsedCacheEvictor(1024 * 1024 * 100),
                StandaloneDatabaseProvider(context),
            )
        }
        return cacheInstance!!
    }
}