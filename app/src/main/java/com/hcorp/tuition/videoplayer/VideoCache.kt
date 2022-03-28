package com.hcorp.tuition.videoplayer

import android.content.Context
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.CacheEvictor
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File


class VideoCache {
    object Singleton {
        private var sDownloadCache: SimpleCache? = null
        fun getInstance(context: Context): SimpleCache {
            val maxCacheSize: Long = 1024 * 1024
            val cacheEvict: CacheEvictor = LeastRecentlyUsedCacheEvictor(maxCacheSize)
            val databaseProvider = StandaloneDatabaseProvider(context)
            if (sDownloadCache == null) sDownloadCache = SimpleCache(
                File(context.cacheDir, context.cacheDir.toString()),
                cacheEvict,
                databaseProvider
            )

            return sDownloadCache as SimpleCache
        }

    }
}