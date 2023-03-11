package tech.pixelw.cling_common

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import java.util.concurrent.CopyOnWriteArrayList

object UpnpServiceManager {

    private var binder: SharedUpnpService.SharedBinder? = null
    private val tempCallbacks = CopyOnWriteArrayList<BinderCallback>()
    private var attachCount = 0
    private var bindCalled = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            service?.let { iBinder ->
                binder = iBinder as SharedUpnpService.SharedBinder
                tempCallbacks.forEach {
                    it.onBinderAttached(binder!!)
                }
                tempCallbacks.clear()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            binder = null
        }

    }

    @Synchronized
    fun attachUpnpService(context: Context, callback: BinderCallback) {
        if (binder != null) {
            callback.onBinderAttached(binder!!)
        } else {
            if (!bindCalled) {
                context.bindService(
                    Intent(context, SharedUpnpService::class.java),
                    connection,
                    Service.BIND_AUTO_CREATE
                )
                bindCalled = true
            }
            tempCallbacks.add(callback)
        }
        attachCount++
    }

    fun detachUpnpService(context: Context, callback: BinderCallback) {
        attachCount--
        if (attachCount <= 0) stopUpnpService(context)
    }

    fun stopUpnpService(context: Context) {
        context.unbindService(connection)
    }


}