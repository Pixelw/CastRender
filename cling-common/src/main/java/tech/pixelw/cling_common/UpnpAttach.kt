package tech.pixelw.cling_common

import android.content.Context

abstract class UpnpAttach : UpnpServiceManager.BinderCallback {

    protected var context: Context? = null
    open fun start(context: Context) {
        this.context = context
        UpnpServiceManager.attachUpnpService(context, this)
    }

    open fun stop(context: Context) {
        UpnpServiceManager.detachUpnpService(context, this)
    }

    open fun pause() {}
}