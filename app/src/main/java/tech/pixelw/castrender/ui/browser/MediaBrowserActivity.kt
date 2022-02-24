package tech.pixelw.castrender.ui.browser

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import tech.pixelw.castrender.R
import tech.pixelw.castrender.databinding.ActivityMediaBrowserBinding
import tech.pixelw.castrender.ui.browser.entity.BrowserItem
import tech.pixelw.dmp_core.DLNAPlayerService
import tech.pixelw.dmp_core.entity.IUpnpDevice
import tech.pixelw.dmp_core.service.SimpleRegListener

class MediaBrowserActivity : AppCompatActivity(), SimpleRegListener {

    private lateinit var binding: ActivityMediaBrowserBinding
    private var service: DLNAPlayerService.BrowserServiceBinder? = null

    private var deviceList = ArrayList<IUpnpDevice>()
    private lateinit var adapter: MediaBrowserAdapter
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
            service = iBinder as DLNAPlayerService.BrowserServiceBinder
            service!!.addListener(this@MediaBrowserActivity)
            service!!.search()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_media_browser)
        setSupportActionBar(binding.toolbar)
        binding.rvBrowser.layoutManager = LinearLayoutManager(this)
        adapter = MediaBrowserAdapter(this)
        binding.rvBrowser.adapter = adapter
        startBrowseService()
    }

    private fun startBrowseService() {
        bindService(
            Intent(this, DLNAPlayerService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun deviceAdded(device: IUpnpDevice) {
        runOnUiThread {
            Log.d(TAG, "deviceAdded() called with: device = $device")
            deviceList.add(device)
            refreshList(deviceList)
        }
    }

    private fun refreshList(list: List<*>) {
        val newlist = ArrayList<BrowserItem>()
        list.forEach {
            if (it is IUpnpDevice){
                newlist.add(BrowserItem.fromDevice(it))
            }
        }
        adapter.submitList(newlist)
    }

    override fun deviceRemoved(device: IUpnpDevice) {
        runOnUiThread {
            Log.d(TAG, "deviceRemoved() called with: device = $device")
            deviceList.remove(device)
            refreshList(deviceList)
        }
    }
    companion object{
        const val TAG ="MediaBrowserActivity"
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
    }
}