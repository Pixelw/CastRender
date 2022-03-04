package tech.pixelw.dmp_core.service

import tech.pixelw.dmp_core.entity.IUpnpDevice

interface DirectoryRegListener {
    fun deviceAdded(device: IUpnpDevice)
    fun deviceRemoved(device: IUpnpDevice)
}