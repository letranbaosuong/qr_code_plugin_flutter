
import 'dart:async';

import 'package:flutter/services.dart';

class PluginCamera {
  static const MethodChannel _channel =
      const MethodChannel('plugin_camera');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String?> get getResultBarcode async {
    final String? stringBarcode = await _channel.invokeMethod('getResultBarcode');
    return stringBarcode;
  }

  static Future<String?> get pauseCamera async {
    return await _channel.invokeMethod('pauseCamera');
  }

  static Future<String?> get resumeCamera async {
    return await _channel.invokeMethod('resumeCamera');
  }
}
