package tech.pixelw.castrender.ui.controller

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import tech.pixelw.castrender.R
import tech.pixelw.castrender.databinding.ActivityMediaControllerBinding
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

    inner class ControlHandler{
        fun playPause(view: View){

        }
        fun prev(view: View){

        }
        fun next(view: View){

        }
        fun volumeUp(view: View){

        }
        fun volumeDown(view: View){

        }
    }
}