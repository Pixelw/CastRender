package tech.pixelw.cling_common

interface BinderCallback {

    /**
     * get the running upnp service instance, for use case specific role
     * to access the service ability.
     */
    fun onBinderAttached(binder: SharedUpnpService.SharedBinder)

    /**
     * when host service is going down, notifying all subscribers.
     */
    fun onBinderDetaching()
}