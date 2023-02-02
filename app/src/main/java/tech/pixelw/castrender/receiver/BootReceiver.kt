package tech.pixelw.castrender.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import tech.pixelw.castrender.CastRenderApp
import tech.pixelw.castrender.feature.render.RenderManager
import tech.pixelw.castrender.feature.settings.Pref

class BootReceiver : BroadcastReceiver() {
    private val sp by lazy {
        PreferenceManager.getDefaultSharedPreferences(CastRenderApp.getAppContext())
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent?.action?.equals(Intent.ACTION_BOOT_COMPLETED) == true) {
            val boolean = sp.getBoolean(Pref.K_AUTOSTART, true)
            if (boolean) {
                val renderService = RenderManager.renderService
                renderService.hello()
            }
        }
    }
}