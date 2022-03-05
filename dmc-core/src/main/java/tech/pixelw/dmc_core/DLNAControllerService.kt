package tech.pixelw.dmc_core

import android.content.Intent
import android.os.IBinder
import android.util.Log
import org.fourthline.cling.android.AndroidUpnpServiceImpl
import org.fourthline.cling.android.FixedAndroidLogHandler
import org.fourthline.cling.controlpoint.ActionCallback
import org.seamless.util.logging.LoggingUtil

class DLNAControllerService : AndroidUpnpServiceImpl() {

    private val mBinder = ControllerServiceBinder()

    override fun onCreate() {
        Log.i(TAG, "onCreate: ")
        LoggingUtil.resetRootHandler(FixedAndroidLogHandler())
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder = mBinder

    override fun onDestroy() {
        Log.w(TAG, "onDestroy: ")
        super.onDestroy()
    }

    inner class ControllerServiceBinder : android.os.Binder() {
        fun search() {
            Log.w(TAG, "search: ")
            upnpService.controlPoint.search()
        }

        fun addListener(iRegistryListener: ControllerRegListener) {
            upnpService.registry.addListener(RegistryListener(iRegistryListener))
        }
        fun control(action: ActionCallback) {
            upnpService.controlPoint.execute(action)
        }
    }

    companion object {
        private const val TAG = "DLNAControllerService"
    }

}