package com.lxf.video_cache

import LXFVideoCacheHostApi
import com.danikula.videocache.HttpProxyCacheServer
import com.danikula.videocache.file.TotalSizeLruDiskUsage

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.io.File

/** VideoCachePlugin */
class VideoCachePlugin : FlutterPlugin, MethodCallHandler {

    private lateinit var videoCacheHostApiImplementation: LXFVideoCacheHostApiImplementation

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        videoCacheHostApiImplementation = LXFVideoCacheHostApiImplementation(flutterPluginBinding)

        LXFVideoCacheHostApi.setUp(
                flutterPluginBinding.binaryMessenger,
                videoCacheHostApiImplementation,
        )
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        videoCacheHostApiImplementation.shutdown()
    }

    override fun onMethodCall(call: MethodCall, result: Result) {}
}

class LXFVideoCacheHostApiImplementation(
        private val flutterPluginBinding: FlutterPlugin.FlutterPluginBinding
) : LXFVideoCacheHostApi {
    private var _cacheSize: Long = 0

    private val _cacheRoot = StorageUtils.getIndividualCacheDirectory(flutterPluginBinding.applicationContext)

    private val diskUsage by lazy {
      if (_cacheSize > 0) {
        TotalSizeLruDiskUsage(_cacheSize)
      } else {
        TotalSizeLruDiskUsage(512 * 1024 * 1024)
      }
    }

    private val httpProxyCacheServer by lazy {
      HttpProxyCacheServer.Builder(flutterPluginBinding.applicationContext)
    }
    /// 懒加载缓存服务
    private val cacheServer by lazy {
      httpProxyCacheServer
        .cacheDirectory(_cacheRoot)
        .diskUsage(diskUsage).build()
    }

    /// 重写并通过 cacheServer 将原 url 转换为具备缓存功能的 url
    override fun convertToCacheProxyUrl(url: String): String {
        return cacheServer.getProxyUrl(url)
    }

    override fun setMaxCacheLength(cacheSize: Long) {
      _cacheSize = cacheSize
    }

    override fun getCacheLength(): Long {
      val files = _cacheRoot.listFiles() ?: return  0
      var totalSize: Long = 0
      for (file in files) {
        totalSize += file.length()
      }
      return totalSize
    }

    override fun deleteAllCaches() {
      val files = _cacheRoot.listFiles() ?: return
      for (file in files) {
        file.delete()
      }
    }

    fun shutdown() {
        cacheServer.shutdown()
    }
}


