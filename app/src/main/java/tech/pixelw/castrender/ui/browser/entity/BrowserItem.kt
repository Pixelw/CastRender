package tech.pixelw.castrender.ui.browser.entity

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import org.fourthline.cling.support.model.item.Item
import tech.pixelw.castrender.CastRenderApp
import tech.pixelw.dmp_core.entity.IUpnpDevice

data class BrowserItem(
    val type: Int,
    val title: String,
    val subtitle: String?,
    val icon: Drawable?
) {
    var id : String? = null
    companion object {

        const val TYPE_DEVICE = 1
        const val TYPE_FOLDER = 2
        const val TYPE_FILE = 3

        @JvmStatic
        fun fromDevice(device: IUpnpDevice): BrowserItem {
            return BrowserItem(
                TYPE_DEVICE,
                device.displayString ?: "unknown",
                device.modelDesc,
                getDrawableFromData(device)
            ).apply { id = device.uDN }
        }

        @JvmStatic
        fun fromMediaItem(item: Item): BrowserItem {
            return BrowserItem(TYPE_FOLDER, item.title, null, null).apply {
                id = item.id
            }
        }

        private fun getDrawableFromData(device: IUpnpDevice): Drawable? {
            val icons = device.mDevice().icons
            if (icons == null || icons.isEmpty()) return null
            return icons[0]?.data?.let {
                BitmapDrawable(
                    CastRenderApp.getAppContext().resources,
                    BitmapFactory.decodeByteArray(it, 0, it.size)
                )
            }
        }
    }
}
