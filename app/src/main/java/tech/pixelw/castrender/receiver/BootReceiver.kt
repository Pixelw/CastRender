package tech.pixelw.castrender.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import tech.pixelw.castrender.feature.render.RenderManager

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent?.action?.equals(Intent.ACTION_BOOT_COMPLETED) == true) {
            val renderService = RenderManager.renderService
            renderService.hello()
        }
    }
}