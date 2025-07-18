package com.opticonscanner

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.util.Log
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule

// Opticon SDK imports
import com.extbcr.scannersdk.BarcodeManager
import com.extbcr.scannersdk.EventListener
import com.extbcr.scannersdk.BarcodeData

class STMOpticonScannerModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    companion object {
        const val MODULE_NAME = "STMOpticonScanner"
        const val TAG = "STMOpticonScannerModule"
    }



    private var barcodeManager: BarcodeManager? = null
    private var eventListener: EventListener? = null
    private var isConnected = false

    override fun getName(): String = MODULE_NAME



    @ReactMethod
    fun initializeScanner(promise: Promise) {
        try {
            barcodeManager = BarcodeManager(reactContext)
            barcodeManager?.init()
            eventListener = object : EventListener {
                override fun onReadData(result: BarcodeData) {
                    val barcode = result.text
                    val codeId = result.codeID
                    sendScanResult(barcode, codeId)
                }
                override fun onTimeout() {}
                override fun onConnect() { isConnected = true }
                override fun onDisconnect() { isConnected = false }
                override fun onStart() {}
                override fun onStop() {}
                override fun onImgBuffer(imgdata: ByteArray?, type: Int) {}
            }
            barcodeManager?.addListener(eventListener)
            promise.resolve("Scanner initialized successfully")
        } catch (e: Exception) {
            promise.reject("INIT_ERROR", "Failed to initialize scanner: ${e.message}")
            Log.e(TAG, "Failed to initialize scanner", e)
        }
    }

    @ReactMethod
    fun startScan(promise: Promise) {
        try {
            if (isConnected) {
                barcodeManager?.startDecode()
                promise.resolve("Scan started")
            } else {
                promise.reject("NOT_CONNECTED", "Scanner not connected")
            }
        } catch (e: Exception) {
            promise.reject("SCAN_ERROR", "Failed to start scan: ${e.message}")
            Log.e(TAG, "Failed to start scan", e)
        }
    }

    @ReactMethod
    fun stopScan(promise: Promise) {
        try {
            if (isConnected) {
                barcodeManager?.stopDecode()
                promise.resolve("Scan stopped")
            } else {
                promise.reject("NOT_CONNECTED", "Scanner not connected")
            }
        } catch (e: Exception) {
            promise.reject("SCAN_ERROR", "Failed to stop scan: ${e.message}")
            Log.e(TAG, "Failed to stop scan", e)
        }
    }

    @ReactMethod
    fun startTrigger(promise: Promise) {
        try {
            Log.d(TAG, "Starting trigger mode")
            startScan(promise)
        } catch (e: Exception) {
            promise.reject("TRIGGER_ERROR", "Failed to start trigger: ${e.message}")
            Log.e(TAG, "Failed to start trigger", e)
        }
    }

    @ReactMethod
    fun stopTrigger(promise: Promise) {
        try {
            Log.d(TAG, "Stopping trigger mode")
            stopScan(promise)
        } catch (e: Exception) {
            promise.reject("TRIGGER_ERROR", "Failed to stop trigger: ${e.message}")
            Log.e(TAG, "Failed to stop trigger", e)
        }
    }

    private fun sendScanResult(barcode: String, codeId: Int) {
        val params = Arguments.createMap()
        params.putString("barcode", barcode)
        params.putInt("codeId", codeId)
        reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit("onScannerResult", params)
        Log.d(TAG, "Scan result sent to React Native: $barcode")
    }

    override fun invalidate() {
        cleanup()
    }

    private fun cleanup() {
        try {
            barcodeManager?.removeListener()
            barcodeManager?.deinit()
            Log.d(TAG, "BarcodeManager cleaned up")
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up BarcodeManager", e)
        }
    }
}
