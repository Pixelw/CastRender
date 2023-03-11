package tech.pixelw.cling_common

import android.content.Context

abstract class UpnpAttachment : BinderCallback {

    protected var context: Context? = null
    open fun start(context: Context) {
        this.context = context
        UpnpServiceManager.attachUpnpService(context, this)
    }

    open fun stop(context: Context) {
        UpnpServiceManager.detachUpnpService(context, this)
    }

    override fun onBinderDetaching() {
        context?.let { stop(it) }
    }

    open fun pause() {}
}