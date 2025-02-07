package com.lxf.video_cache

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File


/**
 * Provides application storage paths
 *
 *
 * See https://github.com/nostra13/Android-Universal-Image-Loader
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
internal object StorageUtils {
    private const val INDIVIDUAL_DIR_NAME = "video-cache"

    /**
     * Returns individual application cache directory (for only video caching from Proxy). Cache directory will be
     * created on SD card *("/Android/data/[app_package_name]/cache/video-cache")* if card is mounted .
     * Else - Android defines cache directory on device's file system.
     *
     * @param context Application context
     * @return Cache [directory][File]
     */
    fun getIndividualCacheDirectory(context: Context): File {
        val cacheDir = getCacheDirectory(context, true)
        return File(cacheDir, INDIVIDUAL_DIR_NAME)
    }

    /**
     * Returns application cache directory. Cache directory will be created on SD card
     * *("/Android/data/[app_package_name]/cache")* (if card is mounted and app has appropriate permission) or
     * on device's file system depending incoming parameters.
     *
     * @param context        Application context
     * @param preferExternal Whether prefer external location for cache
     * @return Cache [directory][File].<br></br>
     * **NOTE:** Can be null in some unpredictable cases (if SD card is unmounted and
     * [Context.getCacheDir()][android.content.Context.getCacheDir] returns null).
     */
    private fun getCacheDirectory(context: Context, preferExternal: Boolean): File {
        var appCacheDir: File? = null
        val externalStorageState = try {
            Environment.getExternalStorageState()
        } catch (e: NullPointerException) { // (sh)it happens
            ""
        }
        if (preferExternal && Environment.MEDIA_MOUNTED == externalStorageState) {
            appCacheDir = getExternalCacheDir(context)
        }
        if (appCacheDir == null) {
            appCacheDir = context.cacheDir
        }
        if (appCacheDir == null) {
            val cacheDirPath = "/data/data/" + context.packageName + "/cache/"
            Log.w(
                "StorageUtils",
                "Can't define system cache directory! '$cacheDirPath%s' will be used."
            )
            appCacheDir = File(cacheDirPath)
        }
        return appCacheDir
    }

    private fun getExternalCacheDir(context: Context): File? {
        val dataDir = File(File(Environment.getExternalStorageDirectory(), "Android"), "data")
        val appCacheDir = File(File(dataDir, context.packageName), "cache")
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                Log.w("StorageUtils", "Unable to create external cache directory")
                return null
            }
        }
        return appCacheDir
    }
}
