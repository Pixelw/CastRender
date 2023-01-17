package tech.pixelw.cling_common

interface BinderCallback {
    fun onBinderAttached(binder: SharedUpnpService.SharedBinder)
}