package tech.pixelw.castrender.ui.controller

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.fourthline.cling.controlpoint.ActionCallback
import org.fourthline.cling.model.ModelUtil
import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.message.UpnpResponse
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.model.types.UDAServiceType
import org.fourthline.cling.support.avtransport.callback.GetMediaInfo
import org.fourthline.cling.support.avtransport.callback.GetPositionInfo
import org.fourthline.cling.support.avtransport.callback.GetTransportInfo
import org.fourthline.cling.support.avtransport.callback.Seek
import org.fourthline.cling.support.model.MediaInfo
import org.fourthline.cling.support.model.PositionInfo
import org.fourthline.cling.support.model.TransportInfo
import org.fourthline.cling.support.renderingcontrol.callback.GetVolume
import tech.pixelw.castrender.utils.LogUtil
import tech.pixelw.dmc_core.ControllerRegListener
import tech.pixelw.dmc_core.IUpnpDevice
import tech.pixelw.dmc_core.RegistryListener
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToLong

class ControllerViewModel : ViewModel(), ControllerRegListener {
    private val deviceLiveData: MutableLiveData<MutableList<IUpnpDevice>> by lazy {
        MutableLiveData<MutableList<IUpnpDevice>>(ArrayList())
    }

    fun getDevices() = deviceLiveData

    val renderStateLiveData: MutableLiveData<RenderState> by lazy {
        MutableLiveData<RenderState>(RenderState("Default"))
    }

    // 给activity刷新用，让其观察
    private val vmCalledAction: MutableLiveData<ActionCallback> by lazy {
        MutableLiveData<ActionCallback>()
    }

    fun getVmCalled() = vmCalledAction

    var refreshJob: Job? = null
    var selectedDevice: IUpnpDevice? = null
    fun selectedAVTransport(): Service<Device<*, *, *>, Service<*, *>>? {
        return selectedDevice?.mDevice()?.findService(UDAServiceType(RegistryListener.AV_TRANS))
    }

    fun selectedRenderControl(): Service<Device<*, *, *>, Service<*, *>>? {
        return selectedDevice?.mDevice()?.findService(UDAServiceType(RegistryListener.RENDER))
    }

    var adjustVolume = -1
    var isUserDraggingSeekBar = false

    override fun deviceAdded(device: IUpnpDevice) {
        Log.d(TAG, "deviceAdded() called with: device = $device")
        deviceLiveData.value?.add(device)
        deviceLiveData.postValue(deviceLiveData.value)
    }

    override fun deviceRemoved(device: IUpnpDevice) {
        Log.d(TAG, "deviceRemoved() called with: device = $device")
        deviceLiveData.value?.remove(device)
        deviceLiveData.postValue(deviceLiveData.value)
    }

    fun onUserDragSlider(value: Float) {
        if (value < 0.0f) {
            isUserDraggingSeekBar = true
        } else {
            isUserDraggingSeekBar = false
            renderStateLiveData.value?.positionInfo?.trackDurationSeconds?.let {
                val newPos = ModelUtil.toTimeString((it * value).roundToLong())
                seek(newPos)
            }
        }

    }

    fun volumeDownValue(): Int {
        if (adjustVolume < 0) {
            adjustVolume = renderStateLiveData.value?.volume!!
        }
        adjustVolume = max(adjustVolume - VOLUME_STEP, 0)
        return adjustVolume
    }

    fun volumeUpValue(): Int {
        if (adjustVolume < 0) {
            adjustVolume = renderStateLiveData.value?.volume!!
        }
        adjustVolume = min(adjustVolume + VOLUME_STEP, 100)
        return adjustVolume
    }

    fun setDevice(device: IUpnpDevice) {
        refreshJob?.cancel()
        selectedDevice = device
        refreshJob = viewModelScope.launch(Dispatchers.Main) {
            var count = 0
            while (true) {
                if (!isUserDraggingSeekBar) updatePosition()
                when {
                    count % 4 == 0 -> {
                        updateMediaInfo()
                        updateTransportInfo()
                    }
                    count % 2 == 0 -> {
                        updateVolume()
                    }
                }
                count++
                delay(UPDATE_INTERVAL)
            }
        }
    }


    //  update to obj renderstate
    fun updatePosition() {
        selectedAVTransport()?.let {
            val act = object : GetPositionInfo(it) {
                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) = LogUtil.e(TAG, defaultMsg)

                override fun received(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    positionInfo: PositionInfo?
                ) {
                    renderStateLiveData.value?.positionInfo = positionInfo
                    renderStateLiveData.postValue(renderStateLiveData.value)
                }
            }
            vmCalledAction.value = act
        }
    }

    fun updateTransportInfo() {
        selectedAVTransport()?.let {
            val act = object : GetTransportInfo(it) {
                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) = LogUtil.e(TAG, defaultMsg)

                override fun received(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    transportInfo: TransportInfo?
                ) {
                    renderStateLiveData.value?.transportInfo = transportInfo
                    renderStateLiveData.postValue(renderStateLiveData.value)
                }

            }
            vmCalledAction.value = act
        }
    }

    // RenderingControl
    fun updateVolume() {
        selectedRenderControl()?.let {
            val act = object : GetVolume(it) {
                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) = LogUtil.e(TAG, defaultMsg)

                override fun received(
                    actionInvocation: ActionInvocation<out Service<*, *>>?,
                    currentVolume: Int
                ) {
                    renderStateLiveData.value?.volume = currentVolume
                    renderStateLiveData.postValue(renderStateLiveData.value)
                }
            }
            vmCalledAction.value = act
        }
    }

    fun updateMediaInfo() {
        selectedAVTransport()?.let {
            val act = object : GetMediaInfo(it) {
                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) = LogUtil.e(TAG, defaultMsg)

                override fun received(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    mediaInfo: MediaInfo?
                ) {
                    renderStateLiveData.value?.mediaInfo = mediaInfo
                    renderStateLiveData.postValue(renderStateLiveData.value)
                }

            }
            vmCalledAction.value = act
        }
    }

    fun seek(newPos: String) {
        selectedAVTransport()?.let {
            val act = object : Seek(it, newPos) {
                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) = LogUtil.e(TAG, defaultMsg)
            }
            vmCalledAction.value = act
        }
    }

    companion object {
        const val TAG = "ControllerVm"
        const val VOLUME_STEP = 7
        const val UPDATE_INTERVAL = 500L
    }

}