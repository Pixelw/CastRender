package tech.pixelw.dmp_core

import android.content.Intent
import android.os.IBinder
import android.util.Log
import org.fourthline.cling.android.AndroidUpnpServiceImpl
import org.fourthline.cling.android.FixedAndroidLogHandler
import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.message.UpnpResponse
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.model.types.UDAServiceType
import org.fourthline.cling.support.contentdirectory.callback.Browse
import org.fourthline.cling.support.model.BrowseFlag
import org.fourthline.cling.support.model.DIDLContent
import org.seamless.util.logging.LoggingUtil
import tech.pixelw.dmp_core.entity.ContentDirectoryCallback
import tech.pixelw.dmp_core.entity.IUpnpDevice
import tech.pixelw.dmp_core.service.SimpleRegListener
import tech.pixelw.dmp_core.service.RegistryListener

open class DLNAPlayerService : AndroidUpnpServiceImpl() {

    private val mBinder = BrowserServiceBinder()

    companion object {
        const val TAG = "DLNAPlayerService"
    }

    override fun onCreate() {
        Log.i(TAG, "onCreate: ")
        LoggingUtil.resetRootHandler(FixedAndroidLogHandler())
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? = mBinder

    override fun onDestroy() {
        Log.w(TAG, "onDestroy: ")
        super.onDestroy()
    }

    inner class BrowserServiceBinder : android.os.Binder() {
        fun search() {
            upnpService.controlPoint.search()
        }

        fun addListener(iRegistryListener: SimpleRegListener) {
            upnpService.registry.addListener(RegistryListener(iRegistryListener))
        }

        fun removeListener(iRegistryListener: SimpleRegListener) {
            upnpService.registry.removeListener(RegistryListener(iRegistryListener))
        }

        fun browse(iUpnpDevice: IUpnpDevice, directoryID: String, callback: ContentDirectoryCallback) {
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
            upnpService.controlPoint.execute(browse)
        }
    }
}