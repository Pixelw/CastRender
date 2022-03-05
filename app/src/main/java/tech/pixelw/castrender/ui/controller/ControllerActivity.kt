package tech.pixelw.castrender.ui.controller

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import org.fourthline.cling.controlpoint.ActionCallback
import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.message.UpnpResponse
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.support.avtransport.callback.Pause
import org.fourthline.cling.support.avtransport.callback.Play
import org.fourthline.cling.support.avtransport.callback.Previous
import tech.pixelw.castrender.R
import tech.pixelw.castrender.databinding.ActivityMediaControllerBinding
import tech.pixelw.castrender.utils.LogUtil
import tech.pixelw.dmc_core.DLNAControllerService

class ControllerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaControllerBinding
    private lateinit var vm: ControllerViewModel
    private var service: DLNAControllerService.ControllerServiceBinder? = null


    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
            service = iBinder as DLNAControllerService.ControllerServiceBinder
            service!!.addListener(vm)
            service!!.search()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_media_controller)
        binding.lifecycleOwner = this
        vm = ViewModelProvider(this).get(ControllerViewModel::class.java)
        startControllerService()
    }

    private fun startControllerService() {
        bindService(
            Intent(this, DLNAControllerService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
    }

    inner class ControlHandler {
        fun playPause(view: View) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            getServiceFromSelected()?.let {
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
                service?.control(action)
            }
        }

        fun prev(view: View) {
            getServiceFromSelected()?.let {
                val act = object : Previous(it){
                    override fun failure(
                        invocation: ActionInvocation<out Service<*, *>>?,
                        operation: UpnpResponse?,
                        defaultMsg: String?
                    ) = LogUtil.e(TAG, defaultMsg)
                }
                service?.control(act)
            }
        }

        fun next(view: View) {

        }

        fun volumeUp(view: View) {

        }

        fun volumeDown(view: View) {

        }
    }

    // TODO:
    private fun isPlaying(): Boolean {
        return false
    }

    private fun getServiceFromSelected(): Service<Device<*, *, *>, Service<*, *>>? {
        return null
    }

    companion object {
        private const val TAG = "ControllerActivity"
    }
}