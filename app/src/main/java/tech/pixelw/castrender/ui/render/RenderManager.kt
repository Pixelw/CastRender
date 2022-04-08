package tech.pixelw.castrender.ui.render

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import tech.pixelw.castrender.BuildConfig
import tech.pixelw.castrender.CastRenderApp
import tech.pixelw.castrender.ui.render.music.MusicPlayerActivity
import tech.pixelw.cling_common.entity.MediaEntity
import tech.pixelw.dmr_core.DLNARendererService
import tech.pixelw.dmr_core.DeviceSettings
import tech.pixelw.dmr_core.R
import tech.pixelw.dmr_core.service.DefaultRenderControl
import tech.pixelw.dmr_core.service.IDLNANewSession

object RenderManager {
    const val CHANNEL_ID = "cast_render_channel"

    val renderService: DLNARendererService by lazy {
        val deviceSettings = DeviceSettings(
            "CastRender (${Build.MODEL})",
            "CastRender by Pixelw",
            BuildConfig.VERSION_CODE,
            "https://github.com/Pixelw/CastRender",
            Build.MODEL,
            Build.MANUFACTURER
        )
        DefaultRenderControl.idlnaNewSession =
            IDLNANewSession { context1: Context?, url: String?, entity: MediaEntity ->
                if (entity.mediaType == MediaEntity.TYPE_AUDIO) {
                    MusicPlayerActivity.newPlayerInstance(context1, url, entity)
                } else {
                    PlayerActivity.newPlayerInstance(context1, url, entity)
                }
            }
        val dlnaRendererService = DLNARendererService(
            deviceSettings,
            createNotification(
                CastRenderApp.getAppContext(),
                "CastRender is Running Background.",
                CHANNEL_ID,
                "CastRender Background Service",
            )
        )
        dlnaRendererService.start(CastRenderApp.getAppContext())
        dlnaRendererService
    }

    @JvmStatic
    fun createNotification(
        context: Context,
        text: String,
        channelId: String,
        channelDesc: String
    ): Notification {
        val managerCompat = NotificationManagerCompat.from(context)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.tv)
            .setContentTitle(text)
            .setTicker(text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelDesc, NotificationManager.IMPORTANCE_MIN)
            managerCompat.createNotificationChannel(channel)
            builder.setChannelId(channelId)
        }
        return builder.build()
    }
}