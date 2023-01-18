package tech.pixelw.castrender.feature.render

import android.support.v4.media.session.PlaybackStateCompat
import org.fourthline.cling.support.model.TransportState
import tech.pixelw.castrender.feature.render.player.IPlayerCallback
import tech.pixelw.dmr_core.DLNARendererService

/**
 * 实现播放器->DMR服务的状态更新
 */
class RenderNotifyImpl(private val binder: DLNARendererService) : IPlayerCallback {
    private var lastTransState = TransportState.CUSTOM

    override fun onPlaybackStateChanged(state: Int) {
        val transportState = state.toTransportState()
        transportState.let {
            if (lastTransState != it) binder.notifyTransportStateChanged(it)
            lastTransState = it
        }
    }


}


internal fun Int.toTransportState(): TransportState {
    return when (this) {
        PlaybackStateCompat.STATE_NONE -> TransportState.NO_MEDIA_PRESENT
        PlaybackStateCompat.STATE_STOPPED, PlaybackStateCompat.STATE_ERROR -> {
            TransportState.STOPPED
        }
        PlaybackStateCompat.STATE_PAUSED -> TransportState.PAUSED_PLAYBACK
        PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.STATE_FAST_FORWARDING,
        PlaybackStateCompat.STATE_REWINDING -> TransportState.PLAYING

        PlaybackStateCompat.STATE_BUFFERING, PlaybackStateCompat.STATE_CONNECTING,
        PlaybackStateCompat.STATE_SKIPPING_TO_NEXT, PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS,
        PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM -> TransportState.TRANSITIONING

        else -> TransportState.CUSTOM
    }
}