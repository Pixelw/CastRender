package tech.pixelw.dmc_core

interface ControllerRegListener {
    fun deviceAdded(device: IUpnpDevice)
    fun deviceRemoved(device: IUpnpDevice)
}