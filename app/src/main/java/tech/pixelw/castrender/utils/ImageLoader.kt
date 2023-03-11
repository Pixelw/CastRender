package tech.pixelw.castrender.utils

//import com.vansuita.gaussianblur.GaussianBlur
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.net.URL

object ImageLoader {
    private const val TAG = "ImageLoader"

//    @JvmStatic
//    suspend fun loadBlurImage(url: String, context: Context): Bitmap? {
//        return withContext(Dispatchers.IO) {
//            syncLoadImageIntoBitmap(url)?.let {
//                syncRenderBlur(it, context)
//            }
//        }
//    }

    fun syncLoadImageIntoBitmap(url: String): Bitmap? {
        kotlin.runCatching {
            BitmapFactory.decodeStream(URL(url).openConnection().apply {
                doInput = true
                connect()
            }.getInputStream())
        }.onSuccess {
            return it
        }.onFailure {
            LogUtil.e(TAG, "loadImageIntoBitmap failed", it)
            return null
        }
        return null
    }

//    @JvmStatic
//    suspend fun renderBlur(bitmap: Bitmap, context: Context, radius: Int = 15): Bitmap {
//        return withContext(Dispatchers.Default) {
//            syncRenderBlur(bitmap, context, radius)
//        }
//    }
//
//    fun syncRenderBlur(bitmap: Bitmap, context: Context, radius: Int = 15): Bitmap {
//        return GaussianBlur.with(context).radius(radius).render(bitmap)
//    }
//

}