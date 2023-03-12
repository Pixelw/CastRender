package tech.pixelw.castrender.utils

import android.os.Build
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import tech.pixelw.castrender.utils.PreLollipopTlsFactory.MyTrustManager

object SharedOkhttpClient {

    val client: OkHttpClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        val builder = OkHttpClient.Builder()
        if (Build.VERSION.SDK_INT in 16..21) {
            try {
                val preLollipopTlsFactory = PreLollipopTlsFactory()
                val myTrustManager = MyTrustManager()
                preLollipopTlsFactory.init(myTrustManager)
                builder.sslSocketFactory(preLollipopTlsFactory, myTrustManager).hostnameVerifier { _, _ -> true }
                    .connectionSpecs(listOf(ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).build()))
            } catch (t: Throwable) {
                LogUtil.e(TAG, "error on init okhttp client", t)
            }
        }
        builder.build()
    }

    private const val TAG = "SharedOkhttpClient"
}