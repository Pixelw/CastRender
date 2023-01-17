package tech.pixelw.castrender.utils.ktx

import android.os.Handler
import android.os.Looper

object HandlerKtx {
    val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    fun Runnable.runOnMain() {
        HandlerKtx.handler.post(this)
    }

    fun Runnable.runOnMainDelayed(millis: Long) {
        handler.removeCallbacks(this)
        HandlerKtx.handler.postDelayed(this, millis)
    }

    fun Runnable.cancel() {
        handler.removeCallbacks(this)
    }

    fun runOnMain(block: () -> Unit) {
        HandlerKtx.handler.post(block)
    }
}

