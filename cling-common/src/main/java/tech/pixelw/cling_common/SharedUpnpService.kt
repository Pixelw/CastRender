package tech.pixelw.cling_common

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import org.fourthline.cling.UpnpService
import org.fourthline.cling.UpnpServiceConfiguration
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration
import org.fourthline.cling.android.AndroidUpnpServiceImpl
import org.fourthline.cling.android.FixedAndroidLogHandler
import org.fourthline.cling.controlpoint.ControlPoint
import org.fourthline.cling.registry.Registry
import org.seamless.util.logging.LoggingUtil

class SharedUpnpService : AndroidUpnpServiceImpl() {

    private val mBinder = SharedBinder()

    val NOTIFIID = 220322

    companion object {
        private const val TAG = "SharedUpnpService"
    }

    override fun createConfiguration(): UpnpServiceConfiguration {
        return object : AndroidUpnpServiceConfiguration() {
            override fun getAliveIntervalMillis() = 5000
            override fun getRegistryMaintenanceIntervalMillis() = 7000
        }
    }

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

    inner class SharedBinder : android.os.Binder() {
        fun getService(): UpnpService {
            return upnpService
        }

        fun getConfiguration(): UpnpServiceConfiguration {
            return upnpService.configuration
        }

        fun getRegistry(): Registry {
            return upnpService.registry
        }

        fun getControlPoint(): ControlPoint {
            return upnpService.controlPoint
        }

        fun getContext(): Context {
            return this@SharedUpnpService
        }

        fun serveForeground(notification: Notification) {
            startForeground(NOTIFIID, notification)
        }
    }

}