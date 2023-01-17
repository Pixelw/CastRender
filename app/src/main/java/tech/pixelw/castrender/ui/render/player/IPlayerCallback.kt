package tech.pixelw.castrender.ui.render.player

import android.support.v4.media.session.PlaybackStateCompat.State

interface IPlayerCallback {
    fun onPlaybackStateChanged(@State state: Int) {}
    fun onIsPlayingChanged(isPlaying: Boolean) {}
    fun onPositionTick(position: Long) {}
}