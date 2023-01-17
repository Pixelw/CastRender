package tech.pixelw.dmc_core

import android.content.Context
import android.util.Log
import org.fourthline.cling.controlpoint.ActionCallback
import org.fourthline.cling.controlpoint.ControlPoint
import org.fourthline.cling.registry.Registry
import tech.pixelw.cling_common.SharedUpnpService
import tech.pixelw.cling_common.UpnpAttachment

class DLNAControllerService : UpnpAttachment() {

    private var controlPoint: ControlPoint? = null
    private var registry: Registry? = null

    var pendingListener: ControllerRegListener? = null
    private var registryListener: RegistryListener? = null

    fun search() {
        Log.w(TAG, "search: ")
        controlPoint?.search()
    }

    fun control(action: ActionCallback) {
        controlPoint?.execute(action)
    }

    override fun stop(context: Context) {
        registry?.removeListener(registryListener)
        registry = null
        controlPoint = null
        super.stop(context)
    }

    override fun onBinderAttached(binder: SharedUpnpService.SharedBinder) {
        registry = binder.getRegistry()
        controlPoint = binder.getControlPoint()
        pendingListener?.let { listener ->
            registryListener = RegistryListener(listener)
            registry?.addListener(registryListener)
            search()
            registry?.let {
                it.remoteDevices.forEach { device ->
                    registryListener!!.remoteDeviceAdded(it, device)
                }
            }
        }
    }

    companion object {
        private const val TAG = "DLNAControllerService"
    }
}
