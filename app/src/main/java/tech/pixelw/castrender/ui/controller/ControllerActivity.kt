package tech.pixelw.castrender.ui.controller

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import org.fourthline.cling.controlpoint.ActionCallback
import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.message.UpnpResponse
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.support.avtransport.callback.Next
import org.fourthline.cling.support.avtransport.callback.Pause
import org.fourthline.cling.support.avtransport.callback.Play
import org.fourthline.cling.support.avtransport.callback.Previous
import org.fourthline.cling.support.model.TransportState
import org.fourthline.cling.support.renderingcontrol.callback.SetVolume
import tech.pixelw.castrender.CastRenderApp
import tech.pixelw.castrender.R
import tech.pixelw.castrender.databinding.ActivityMediaControllerBinding
import tech.pixelw.castrender.utils.LogUtil
import tech.pixelw.dmc_core.DLNAControllerService

class ControllerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaControllerBinding
    private lateinit var vm: ControllerViewModel
    private var service: DLNAControllerService = DLNAControllerService()
    private lateinit var arrayAdapter: ArrayAdapter<CharSequence>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_media_controller)
        binding.lifecycleOwner = this
        vm = ViewModelProvider(this).get(ControllerViewModel::class.java)
        arrayAdapter = ArrayAdapter(this, R.layout.item_device_select)
        observeVm()
        binding.outlinedExposedDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val find = vm.getDevices().value?.get(position)
                find?.let { vm.setDevice(it) }
            }
        binding.vm = vm
        binding.handler = ControlHandler()
        service.pendingListener = vm
        service.start(CastRenderApp.getAppContext())
    }

    private fun observeVm() {
        vm.getDevices().observe(this) {
            arrayAdapter.clear()
            for (device in it) {
                arrayAdapter.add(device.friendlyName)
            }
            arrayAdapter.notifyDataSetChanged()
            binding.outlinedExposedDropdown.setAdapter(arrayAdapter)
        }
        vm.getVmCalled().observe(this) {
            service.control(it)
        }
    }

    inner class ControlHandler {
        fun playPause(view: View) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vm.selectedAVTransport()?.let {
                val action: ActionCallback
                if (!isPlaying()) {
                    action = object : Play(it) {
                        override fun failure(
                            invocation: ActionInvocation<out Service<*, *>>?,
                            operation: UpnpResponse?,
                            defaultMsg: String?
                        ) = LogUtil.e(TAG, defaultMsg)
                    }
                } else {
                    action = object : Pause(it) {
                        override fun failure(
                            invocation: ActionInvocation<out Service<*, *>>?,
                            operation: UpnpResponse?,
                            defaultMsg: String?
                        ) = LogUtil.e(TAG, defaultMsg)
                    }

                }
                service.control(action)
            }
        }

        fun prev(view: View) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vm.selectedAVTransport()?.let {
                val act = object : Previous(it) {
                    override fun failure(
                        invocation: ActionInvocation<out Service<*, *>>?,
                        operation: UpnpResponse?,
                        defaultMsg: String?
                    ) = LogUtil.e(TAG, defaultMsg)
                }
                service.control(act)
            }
        }

        fun next(view: View) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vm.selectedAVTransport()?.let {
                val act = object : Next(it) {
                    override fun failure(
                        invocation: ActionInvocation<out Service<*, *>>?,
                        operation: UpnpResponse?,
                        defaultMsg: String?
                    ) = LogUtil.e(TAG, defaultMsg)
                }
                service.control(act)
            }
        }

        // RenderingControl
        fun volumeUp(view: View) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vm.selectedRenderControl()?.let {
                val act = object : SetVolume(it, vm.volumeUpValue().toLong()) {
                    override fun failure(
                        invocation: ActionInvocation<out Service<*, *>>?,
                        operation: UpnpResponse?,
                        defaultMsg: String?
                    ) = LogUtil.e(TAG, defaultMsg)
                }
                service.control(act)
            }
        }

        // RenderingControl
        fun volumeDown(view: View) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vm.selectedRenderControl()?.let {
                val act = object : SetVolume(it, vm.volumeDownValue().toLong()) {
                    override fun failure(
                        invocation: ActionInvocation<out Service<*, *>>?,
                        operation: UpnpResponse?,
                        defaultMsg: String?
                    ) = LogUtil.e(TAG, defaultMsg)
                }
                service.control(act)
            }
        }

    }

    private fun isPlaying(): Boolean {
        return vm.renderStateLiveData.value?.transportInfo?.currentTransportState == TransportState.PLAYING
    }

    companion object {
        private const val TAG = "ControllerActivity"
    }
}