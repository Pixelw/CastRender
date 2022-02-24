package tech.pixelw.castrender.discovery

import android.util.Log
import tech.pixelw.dmp_core.service.SimpleRegListener
import tech.pixelw.dmp_core.entity.IUpnpDevice

class DiscoveryReceiver: SimpleRegListener {
    override fun deviceAdded(device: IUpnpDevice) {
        Log.d(TAG, "deviceAdded() called with: device = $device")
    }

    override fun deviceRemoved(device: IUpnpDevice) {
        Log.d(TAG, "deviceRemoved() called with: device = $device")
    }
    companion object{
        const val TAG = "Discovery"
    }
}