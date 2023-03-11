package tech.pixelw.castrender.utils

import android.os.Build
import okhttp3.OkHttpClient

object SharedOkhttpClient {

    val client: OkHttpClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        val builder = OkHttpClient.Builder()
        if (Build.VERSION.SDK_INT in 16..21) {
            builder.socketFactory(PreLollipopTlsFactory())
        }
        builder.build()
    }
}