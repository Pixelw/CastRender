package tech.pixelw.cling_common

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder

object UpnpServiceManager {

    private var binder: SharedUpnpService.SharedBinder? = null
    private val callbacks = mutableListOf<BinderCallback>()
    private var bindCalled = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            service?.let { iBinder ->
                binder = iBinder as SharedUpnpService.SharedBinder
                callbacks.forEach {
                    it.onBinderAttached(binder!!)
                }
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
            callbacks.add(callback)
        }
    }

    fun detachUpnpService(context: Context, callback: BinderCallback) {
        val removed = callbacks.remove(callback)
        if (removed && callbacks.size == 0) {
            stopUpnpService(context)
        }
    }

    fun stopUpnpService(context: Context) {
        context.unbindService(connection)
    }


}