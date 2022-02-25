package tech.pixelw.castrender.ui.browser

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.fourthline.cling.support.model.DIDLContent
import org.fourthline.cling.support.model.DIDLObject
import org.fourthline.cling.support.model.container.Container
import org.fourthline.cling.support.model.item.Item
import tech.pixelw.castrender.ui.browser.entity.BrowserItem
import tech.pixelw.dmp_core.entity.ContentDirectoryCallback
import tech.pixelw.dmp_core.entity.IUpnpDevice
import tech.pixelw.dmp_core.service.SimpleRegListener
import java.util.*
import kotlin.collections.ArrayList

class BrowserViewModel : ViewModel(), SimpleRegListener, ContentDirectoryCallback {
    // 两个元数据
    private val deviceLiveData: MutableLiveData<MutableList<IUpnpDevice>> by lazy {
        MutableLiveData<MutableList<IUpnpDevice>>(ArrayList())
    }
    private val contentLiveData: MutableLiveData<MutableList<DIDLObject>> by lazy {
        MutableLiveData<MutableList<DIDLObject>>(ArrayList())
    }
    // 显示列表
    private val displayListLiveData: MutableLiveData<ArrayList<BrowserItem>> by lazy {
        MutableLiveData<ArrayList<BrowserItem>>(ArrayList())
    }

    fun getDisplays() = displayListLiveData

    val idStack = LinkedList<String>()
    var listState = LIST_STATE_DEVICES
    var currentDevice: IUpnpDevice? = null

    init {
        // 观察元数据, 合成列表
        deviceLiveData.observeForever {
            if (listState == LIST_STATE_DEVICES && it != null) {
                val new = ArrayList<BrowserItem>()
                it.forEach { item ->
                    new.add(BrowserItem.fromDevice(item))
                }
                displayListLiveData.value = new
            }
        }
        contentLiveData.observeForever {
            if (listState == LIST_STATE_CONTENTS && it != null) {
                val new = ArrayList<BrowserItem>()
                new.add(BrowserItem.back())
                it.forEach { item ->
                    if (item is Container) {
                        new.add(BrowserItem.fromMediaContainer(item))
                    }
                    if (item is Item) {
                        new.add(BrowserItem.fromMediaItem(item))
                    }
                }
                displayListLiveData.value = new
            }
        }
    }

    // 来自服务的回调 在子线程
    override fun deviceAdded(device: IUpnpDevice) {
        Log.d(TAG, "deviceAdded() called with: device = $device")
        val add = deviceLiveData.value
        add?.add(device)
        deviceLiveData.postValue(add)
    }

    override fun deviceRemoved(device: IUpnpDevice) {
        Log.d(TAG, "deviceRemoved() called with: device = $device")
        val value = deviceLiveData.value
        value?.remove(device)
        deviceLiveData.postValue(value)
    }

    override fun setContent(didl: DIDLContent?) {
        Log.d(TAG, "setContent() called with: didl = $didl")
        val arrayList = ArrayList<DIDLObject>()
        var idStored = false
        didl?.containers?.let {
            if (it.isEmpty()) return@let
            if (!idStored && checkStackTopNotTheSame(it[0].parentID)) {
                idStack.push(it[0].parentID)
                idStored = true
            }
            arrayList.addAll(it)
        }
        didl?.items?.let {
            if (it.isEmpty()) return@let
            if (!idStored && checkStackTopNotTheSame(it[0].parentID)) {
                idStack.push(it[0].parentID)
                idStored = true
            }
            arrayList.addAll(it)
        }
        contentLiveData.postValue(arrayList)
    }

    private fun checkStackTopNotTheSame(parentID: String?): Boolean {
        if (idStack.isEmpty()) return true
        parentID?.let {
            return idStack[0] != it
        }
        return false
    }

    fun vmGoIntoDevice(item: BrowserItem) {
        listState = LIST_STATE_CONTENTS
        currentDevice = item.obj as IUpnpDevice
    }

    fun vmGoBackDevice() {
        listState = LIST_STATE_DEVICES
        currentDevice = null
        deviceLiveData.value = deviceLiveData.value
    }


    companion object {
        const val TAG = "BrowserVm"
        const val LIST_STATE_DEVICES = 1
        const val LIST_STATE_CONTENTS = 2
    }


}