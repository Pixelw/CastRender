package tech.pixelw.dmp_core.service

import tech.pixelw.cling_common.entity.IUpnpDevice

interface DirectoryRegListener {
    fun deviceAdded(device: IUpnpDevice)
    fun deviceRemoved(device: IUpnpDevice)
}