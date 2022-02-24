package tech.pixelw.dmp_core.service

import tech.pixelw.dmp_core.entity.IUpnpDevice

interface SimpleRegListener {
    fun deviceAdded(device: IUpnpDevice)
    fun deviceRemoved(device: IUpnpDevice)
}