package tech.pixelw.dmc_core

import tech.pixelw.cling_common.entity.IUpnpDevice

interface ControllerRegListener {
    fun deviceAdded(device: IUpnpDevice)
    fun deviceRemoved(device: IUpnpDevice)
}