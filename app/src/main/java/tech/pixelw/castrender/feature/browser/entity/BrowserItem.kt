package tech.pixelw.castrender.feature.browser.entity

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import org.fourthline.cling.support.model.container.Container
import org.fourthline.cling.support.model.item.Item
import tech.pixelw.castrender.CastRenderApp
import tech.pixelw.cling_common.entity.IUpnpDevice

data class BrowserItem(
    val type: Int,
    val title: String,
    val subtitle: String?,
    val icon: Drawable?
) {
    var id: String? = null
    var obj: Any? = null

    companion object {

        const val TYPE_DEVICE = 1
        const val TYPE_FOLDER = 2
        const val TYPE_FILE = 3
        const val TYPE_BACK = 4

        @JvmStatic
        fun fromDevice(device: IUpnpDevice): BrowserItem {
            return BrowserItem(
                TYPE_DEVICE,
                device.displayString ?: "unknown",
                device.modelDesc,
                getDrawableFromData(device)
            ).apply { id = device.uDN; obj = device }
        }

        @JvmStatic
        fun fromMediaItem(item: Item): BrowserItem {
            return BrowserItem(TYPE_FILE, item.title, null, null).apply {
                id = item.id
                obj = item
            }
        }

        @JvmStatic
        fun fromMediaContainer(container: Container): BrowserItem {
            return BrowserItem(TYPE_FOLDER, container.title, null, null).apply {
                id = container.id
                obj = container
            }
        }

        fun back(): BrowserItem {
            return BrowserItem(TYPE_BACK, ".. ", null, null)
        }

        private fun getDrawableFromData(device: IUpnpDevice): Drawable? {
            val icons = device.mDevice().icons
            if (icons == null || icons.isEmpty()) return null
            return icons[0]?.data?.let {
                BitmapDrawable(
                    CastRenderApp.appContext.resources,
                    BitmapFactory.decodeByteArray(it, 0, it.size)
                )
            }
        }
    }
}
