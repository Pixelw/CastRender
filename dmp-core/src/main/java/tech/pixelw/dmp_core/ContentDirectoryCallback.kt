package tech.pixelw.dmp_core

import org.fourthline.cling.model.message.UpnpResponse
import org.fourthline.cling.support.contentdirectory.callback.Browse
import org.fourthline.cling.support.model.DIDLContent

interface ContentDirectoryCallback {
    fun setContent(didl: DIDLContent?)
    fun updateState(status: Browse.Status?){}
    fun failure(operation: UpnpResponse?, defaultMsg: String?){}
}
