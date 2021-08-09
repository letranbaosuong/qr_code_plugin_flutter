package com.example.plugin_camera

import android.app.Activity
import android.util.Log
import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.platform.PlatformViewRegistry

/** PluginCameraPlugin */
class PluginCameraPlugin: FlutterPlugin, /*MethodCallHandler,*/ ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
//  private lateinit var Shared.channel : MethodChannel

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    /*Shared.channel = MethodChannel(flutterPluginBinding.binaryMessenger, "plugin_camera")
    Shared.channel.setMethodCallHandler(this)*/
    onAttachedToEngines(flutterPluginBinding.platformViewRegistry, flutterPluginBinding.binaryMessenger, Shared.activity)
  }

  private fun onAttachedToEngines(platformViewRegistry: PlatformViewRegistry, messenger: BinaryMessenger, activity: Activity?) {
    if (activity != null) {
      Shared.activity = activity
    }
    platformViewRegistry
      .registerViewFactory("plugin_camera_view", NativeViewFactory(messenger/*, Shared.channel*/))
  }

  /*override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
        "getPlatformVersion" -> {
          result.success("Android ${android.os.Build.VERSION.RELEASE}")
        }
        *//*"test" -> {
          result.success("ok")
        }*//*
        else -> {
          result.notImplemented()
        }
    }
  }*/

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
//    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    Shared.activity = binding.activity
    Shared.binding = binding
  }

  override fun onDetachedFromActivityForConfigChanges() {
    Shared.activity = null
    Shared.binding = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    Shared.activity = binding.activity
    Shared.binding = binding
  }

  override fun onDetachedFromActivity() {
    Shared.activity = null
    Shared.binding = null
  }
}
