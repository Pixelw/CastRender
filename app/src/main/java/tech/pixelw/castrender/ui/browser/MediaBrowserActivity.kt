package tech.pixelw.castrender.ui.browser

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import org.fourthline.cling.support.model.DIDLObject
import org.fourthline.cling.support.model.item.Item
import tech.pixelw.castrender.R
import tech.pixelw.castrender.databinding.ActivityMediaBrowserBinding
import tech.pixelw.castrender.ui.browser.entity.BrowserItem
import tech.pixelw.castrender.ui.render.PlayerActivity
import tech.pixelw.dmp_core.DLNAPlayerService
import tech.pixelw.dmp_core.entity.IUpnpDevice

class MediaBrowserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBrowserBinding
    private lateinit var vm: BrowserViewModel
    private var service: DLNAPlayerService.BrowserServiceBinder? = null

    private lateinit var adapter: MediaBrowserAdapter

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
            service = iBinder as DLNAPlayerService.BrowserServiceBinder
            service!!.addListener(vm)
            service!!.search()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_media_browser)
        binding.lifecycleOwner = this
        setSupportActionBar(binding.toolbar)
        vm = ViewModelProvider(this).get(BrowserViewModel::class.java)
        binding.rvBrowser.layoutManager = LinearLayoutManager(this)
        adapter = MediaBrowserAdapter(this)
        adapter.itemClickHandler = ItemHandler()
        binding.rvBrowser.adapter = adapter
        observeVm()
        startBrowseService()
    }

    private fun startBrowseService() {
        bindService(
            Intent(this, DLNAPlayerService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.menu_browser, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> service?.search()
        }
        return true
    }

    private fun observeVm() {
        vm.getDisplays().observe(this) {
            adapter.submitList(it)
        }
    }

    companion object {
        const val TAG = "MediaBrowserActivity"
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
    }

    inner class ItemHandler {
        fun onItemClick(item: BrowserItem) {
            when (item.type) {
                BrowserItem.TYPE_BACK -> goBack()
                BrowserItem.TYPE_DEVICE -> goIntoDevice(item)
                BrowserItem.TYPE_FOLDER -> goIntoFolder(item)
                BrowserItem.TYPE_FILE -> goIntoFile(item)
            }
        }
    }

    fun goBack() {
        service?.let {
            if (!vm.idStack.isEmpty() && vm.idStack[0] != "0") {
                vm.idStack.pop()
                val id = vm.idStack[0]
                it.browse(vm.currentDevice!!, id, vm)
            } else {
                vm.vmGoBackDevice()
                it.search()
            }
        }
    }

    fun goIntoDevice(item: BrowserItem) {
        vm.vmGoIntoDevice(item)
        service?.browse(item.obj as IUpnpDevice, "0", vm)
    }

    fun goIntoFolder(item: BrowserItem) {
        if (vm.currentDevice != null && item.obj is DIDLObject) {
            service?.browse(vm.currentDevice!!, (item.obj as DIDLObject).id, vm)
        }
    }

    fun goIntoFile(item: BrowserItem) {
        val mediaItem = item.obj as Item
        val url = mediaItem.firstResource?.value
        PlayerActivity.newPlayerInstance(this, url)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && vm.idStack.isNotEmpty()) {
            goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}