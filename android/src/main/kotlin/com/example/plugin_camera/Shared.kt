package com.example.plugin_camera

import android.app.Activity
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodChannel

object Shared {
    const val CAMERA_REQUEST_ID = 113114115
    var activity: Activity? = null
    var binding: ActivityPluginBinding? = null
//    lateinit var channel : MethodChannel
}