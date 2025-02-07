import 'package:video_cache/plugin/pigeon.g.dart';

class VideoCache {
  VideoCache._internal();

  factory VideoCache() => _instance;

  static final VideoCache _instance = VideoCache._internal();

  final LXFVideoCacheHostApi _hostApi = LXFVideoCacheHostApi();

  /// 转换为缓存代理URL
  Future<String> convertToCacheProxyUrl(String url) async {
    return _hostApi.convertToCacheProxyUrl(url);
  }

  /// 设置可用的缓存大小
  Future setMaxCacheLength(int cacheSize) async {
    return _hostApi.setMaxCacheLength(cacheSize);
  }

  /// 获取当前缓存大小
  Future<int> getCacheLength() async {
    return _hostApi.getCacheLength();
  }

  /// 删除所有的缓存
  Future deleteAllCaches() async {
    return _hostApi.deleteAllCaches();
  }
}
