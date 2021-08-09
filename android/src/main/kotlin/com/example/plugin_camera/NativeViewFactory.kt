package com.example.plugin_camera

import android.content.Context
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class NativeViewFactory (private val messenger: BinaryMessenger/*, private val methodChannel: MethodChannel*/) : PlatformViewFactory (
    StandardMessageCodec.INSTANCE
) {
    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        val creationParams = args as Map<String?, Any?>?
        return NativeView(context, messenger, /*methodChannel,*/ viewId, creationParams)
    }
}