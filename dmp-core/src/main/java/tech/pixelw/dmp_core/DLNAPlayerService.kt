package tech.pixelw.dmp_core

import android.content.Context
import android.util.Log
import org.fourthline.cling.controlpoint.ControlPoint
import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.message.UpnpResponse
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.model.types.UDAServiceType
import org.fourthline.cling.registry.Registry
import org.fourthline.cling.support.contentdirectory.callback.Browse
import org.fourthline.cling.support.model.BrowseFlag
import org.fourthline.cling.support.model.DIDLContent
import tech.pixelw.cling_common.SharedUpnpService
import tech.pixelw.cling_common.UpnpAttach
import tech.pixelw.cling_common.entity.IUpnpDevice
import tech.pixelw.dmp_core.service.DirectoryRegListener
import tech.pixelw.dmp_core.service.RegistryListener

open class DLNAPlayerService : UpnpAttach() {
    private var registry: Registry? = null
    private var controlPoint: ControlPoint? = null
    var pendingListener: DirectoryRegListener? = null
    private var registryListener: RegistryListener? = null

    override fun stop(context: Context) {
        registry?.removeListener(registryListener)
        controlPoint = null
        registry = null
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
                it.remoteDevices?.forEach { dev ->
                    registryListener!!.remoteDeviceAdded(it, dev)
                }
            }
        }
    }

    companion object {
        const val TAG = "DLNAPlayerService"
    }

    fun search() {
        controlPoint?.search()
        Log.w(TAG, "search: ")
    }

    fun browse(iUpnpDevice: IUpnpDevice, directoryID: String, callback: ContentDirectoryCallback) {
        Log.w(TAG, "browse: ")
        val service = iUpnpDevice.mDevice().findService(UDAServiceType("ContentDirectory"))
        val browse = object : Browse(service, directoryID, BrowseFlag.DIRECT_CHILDREN) {
            override fun failure(
                invocation: ActionInvocation<out Service<*, *>>?,
                operation: UpnpResponse?,
                defaultMsg: String?
            ) {
                callback.failure(operation, defaultMsg)
            }

            override fun received(
                actionInvocation: ActionInvocation<out Service<*, *>>?,
                didl: DIDLContent?
            ) {
                callback.setContent(didl)
            }

            override fun updateStatus(status: Status?) {
                callback.updateState(status)
            }

        }
        controlPoint?.execute(browse)
    }

}