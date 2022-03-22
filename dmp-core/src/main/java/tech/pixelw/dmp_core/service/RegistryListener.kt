package tech.pixelw.dmp_core.service

import android.util.Log
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.meta.LocalDevice
import org.fourthline.cling.model.meta.RemoteDevice
import org.fourthline.cling.registry.DefaultRegistryListener
import org.fourthline.cling.registry.Registry
import tech.pixelw.cling_common.entity.CDevice

class RegistryListener(private val iRegistryListener: DirectoryRegListener) :
    DefaultRegistryListener() {

    override fun remoteDeviceAdded(registry: Registry?, device: RemoteDevice?) {
        Log.i(TAG, "remoteDeviceAdded: " + device?.displayString)
        mDeviceAdded(registry, device)
        super.remoteDeviceAdded(registry, device)
    }

    override fun remoteDeviceRemoved(registry: Registry?, device: RemoteDevice?) {
        Log.i(TAG, "remoteDeviceRemoved: " + device?.displayString)
        mDeviceRemoved(registry, device)
        super.remoteDeviceRemoved(registry, device)
    }

    override fun localDeviceAdded(registry: Registry?, device: LocalDevice?) {
        Log.i(TAG, "localDeviceAdded: " + device?.displayString)
        mDeviceAdded(registry, device)
        super.localDeviceAdded(registry, device)
    }

    override fun localDeviceRemoved(registry: Registry?, device: LocalDevice?) {
        Log.i(TAG, "localDeviceRemoved: " + device?.displayString)
        mDeviceAdded(registry, device)
        super.localDeviceRemoved(registry, device)
    }

    fun mDeviceAdded(registry: Registry?, device: Device<*, *, *>?) {
        filter(device)?.let {
            iRegistryListener.deviceAdded(it)
        }
    }

    fun mDeviceRemoved(registry: Registry?, device: Device<*, *, *>?) {
        device?.let {
            iRegistryListener.deviceRemoved(CDevice(it))
        }
    }

    fun filter(device: Device<*, *, *>?): CDevice? {
        return device?.let {
            val cDevice = CDevice(it)
            if (cDevice.asService(CONTENT)) cDevice else null
        }
    }

    companion object {
        const val TAG = "RegistryListener"
        const val CONTENT = "ContentDirectory"
    }
}