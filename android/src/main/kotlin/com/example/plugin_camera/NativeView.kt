package com.example.plugin_camera

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.camera2.CameraCharacteristics
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.BarcodeView
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.platform.PlatformView

internal class NativeView(context: Context, messenger: BinaryMessenger, /*private val methodChannel: MethodChannel,*/ id: Int, private val params: Map<String?, Any?>?) :
    PlatformView, MethodChannel.MethodCallHandler, PluginRegistry.RequestPermissionsResultListener,
    FlutterActivity() {
    /*private val textView: TextView = TextView(context)*/

    private var isTorchOn: Boolean = false
    private var isPaused: Boolean = false
    private var barcodeView: BarcodeView? = null
    private val channel: MethodChannel = MethodChannel(messenger, "plugin_camera")
    private var permissionGranted: Boolean = false

    override fun getView(): View {
        /*return textView*/
        return initBarCodeView().apply {}!!
    }

    init {
        /*textView.textSize = 72f
        textView.setBackgroundColor(Color.rgb(255, 255, 255))
        textView.text = "Rendered on a native Android view (id: $id)"*/

        if (Shared.binding != null) {
            Shared.binding!!.addRequestPermissionsResultListener(this)
        }

//        channel = MethodChannel(messenger, "plugin_camera")
        channel.setMethodCallHandler(this)
        Shared.activity?.application?.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                /*if (activity == Shared.activity && !isPaused && hasCameraPermission()) {
                    barcodeView?.resume()
                }*/
                Log.d("NativeView", "onActivityCreated: ")
            }

            override fun onActivityStarted(activity: Activity) {
                /*if (activity == Shared.activity && !isPaused && hasCameraPermission()) {
                    barcodeView?.resume()
                }*/
                Log.d("NativeView", "onActivityStarted: ")
            }

            override fun onActivityResumed(activity: Activity) {
                if (activity == Shared.activity && !isPaused && hasCameraPermission()) {
                    barcodeView?.resume()
                }
                Log.d("NativeView", "onActivityResumed: ")
            }

            override fun onActivityPaused(activity: Activity) {
                if (activity == Shared.activity && !isPaused && hasCameraPermission()) {
                    barcodeView?.pause()
                }
                Log.d("NativeView", "onActivityPaused: ")
            }

            override fun onActivityStopped(activity: Activity) {
                /*if (activity == Shared.activity && !isPaused && hasCameraPermission()) {
                    barcodeView?.pause()
                }*/
                Log.d("NativeView", "onActivityStopped: ")
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                /*if (activity == Shared.activity && !isPaused && hasCameraPermission()) {
                    barcodeView?.pause()
                }*/
                Log.d("NativeView", "onActivitySaveInstanceState: ")
            }

            override fun onActivityDestroyed(activity: Activity) {
                /*if (activity == Shared.activity && !isPaused && hasCameraPermission()) {
                    barcodeView?.resume()
                }*/
                Log.d("NativeView", "onActivityDestroyed: ")
            }
        })
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when(call.method) {
            "getPlatformVersion" -> result.success("Android ${Build.VERSION.RELEASE}")
            "startScan" -> startScan(call.arguments as? List<Int>, result)
            "getResultBarcode" -> {
                /*IntentIntegrator(Shared.activity).apply {
                    setDesiredBarcodeFormats(IntentIntegrator.QR_CODE) //Chọn type của BarCode
                    setPrompt("Đây là message thông báo hiển thị trên màn hình capture")
                    setCameraId(0) //Id của camera sử dụng để thực hiện scan
                    setBeepEnabled(false) //Âm thanh khi thực hiện scan
                    setOrientationLocked(false) //cố đinh chiều của camera
                    //vân vân và mây mây
                    initiateScan() //bắt đầu scan
                }*/
                //IntentIntegrator(Shared.activity).initiateScan() // `this` is the current Activity
                barcodeView?.decodeSingle (object: BarcodeCallback {
                    override fun barcodeResult(resultBarcode: BarcodeResult?) {
                        resultBarcode?.let {
                            Log.d("NativeView", "barcodeResult: ${it.text}")
                            result.success(it.text)
                        }
                    }

                    override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
                    }
                })
            }
            else -> result.notImplemented()
        }
    }

    private fun startScan(arguments: List<Int>?, result: MethodChannel.Result) {
        val allowedBarcodeTypes = mutableListOf<BarcodeFormat>()
        try {
            checkAndRequestPermission(result)

            arguments?.forEach {
                allowedBarcodeTypes.add(BarcodeFormat.values()[it])
            }
        } catch (e: java.lang.Exception) {
            result.error(null, null, null)
        }

        barcodeView?.decodeContinuous(
            object : BarcodeCallback {
                override fun barcodeResult(result: BarcodeResult) {
                    if (allowedBarcodeTypes.size == 0 || allowedBarcodeTypes.contains(result.barcodeFormat)) {
                        val code = mapOf(
                            "code" to result.text,
                            "type" to result.barcodeFormat.name,
                            "rawBytes" to result.rawBytes)
                        channel.invokeMethod("onRecognizeQR", code)
                    }

                }

                override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
            }
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>?,
        grantResults: IntArray?
    ): Boolean {
        if (requestCode == Shared.CAMERA_REQUEST_ID && grantResults?.isNotEmpty() == true && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionGranted = true
            channel.invokeMethod("onPermission", true)
            return true
        }
        permissionGranted = false
        channel.invokeMethod("onPermission", false)
        return false
    }

    override fun dispose() {
        barcodeView?.pause()
        barcodeView = null
    }

    private fun hasCameraPermission(): Boolean {
        return permissionGranted ||
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
            Shared.activity?.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun initBarCodeView(): BarcodeView? {
        if (barcodeView == null) {
            barcodeView = BarcodeView(Shared.activity)
            barcodeView?.cameraSettings?.requestedCameraId = CameraCharacteristics.LENS_FACING_FRONT
            barcodeView!!.resume()
        } else {
            if (hasCameraPermission()) {
                if (!isPaused) barcodeView!!.resume()
            } else {
                checkAndRequestPermission(null)
            }
        }

        return barcodeView
        /*if (barcodeView == null) {
            barcodeView = BarcodeView(Shared.activity)
            *//*if (params!!["cameraFacing"] as Int == 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    barcodeView?.cameraSettings?.requestedCameraId = CameraCharacteristics.LENS_FACING_FRONT
                    barcodeView!!.resume()
                }
            }*//*
            barcodeView?.cameraSettings?.requestedCameraId = CameraCharacteristics.LENS_FACING_FRONT
            barcodeView!!.resume()
        } else {
            if (!isPaused) barcodeView!!.resume()
        }
        return barcodeView*/
    }

    private fun checkAndRequestPermission(result: MethodChannel.Result?) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                if (Shared.activity?.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true
                    channel.invokeMethod("onPermissionSet", true)
                } else {
                    Shared.activity?.requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        Shared.CAMERA_REQUEST_ID)
                }
            }
            else -> {
                result?.error("cameraPermission", "Platform Version to low for camera permission check", null)
            }
        }
    }
}
