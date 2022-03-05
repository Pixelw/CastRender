package tech.pixelw.castrender.ui.controller

import androidx.lifecycle.ViewModel
import tech.pixelw.dmc_core.ControllerRegListener
import tech.pixelw.dmc_core.IUpnpDevice

class ControllerViewModel: ViewModel(), ControllerRegListener {

    override fun deviceAdded(device: IUpnpDevice) {
    }

    override fun deviceRemoved(device: IUpnpDevice) {
    }


    // TODO:  check auto update RenderCommand::updateFull()
    fun onUserDragSlider(value: Float){

    }
}