package tech.pixelw.castrender.feature.render.player

import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.State
import android.view.View
import androidx.collection.ArraySet
import kotlin.math.max
import kotlin.math.min

abstract class IPlayer<P> {

    open val positionPollInterval = 250L

    var position: Long = 0
        protected set(value) {
            field = value
            callbacks.forEach {
                it.onPositionTick(value)
            }
        }

    @State
    var mediaSessionState: Int = PlaybackStateCompat.STATE_NONE
        protected set(value) {
            if (value != field) {
                field = value
                callbacks.forEach {
                    it.onPlaybackStateChanged(value)
                }
            }
        }

    protected val callbacks = ArraySet<IPlayerCallback>()

    abstract fun playerInstance(): P

    abstract fun bindView(view: View)

    abstract fun prepareMedia(url: String, playWhenReady: Boolean = true)

    abstract fun play()

    abstract fun pause()

    abstract fun stop()

    abstract fun getDuration(): Long

    abstract fun isPlaying(): Boolean

    abstract fun close()

    abstract var speed: Float

    abstract fun seekTo(millis: Long)

    fun addPlayerCallback(iPlayerCallback: IPlayerCallback) {
        callbacks.add(iPlayerCallback)
    }

    open fun seekBack(length: Long = 5000) {
        val pos = max(0, position - length)
        seekTo(pos)
    }

    open fun seekForward(length: Long = 5000) {
        val pos = min(getDuration(), position + length)
        seekTo(pos)
    }

}

fun Int.isReady(): Boolean {
    return when (this) {
        PlaybackStateCompat.STATE_NONE, PlaybackStateCompat.STATE_STOPPED, PlaybackStateCompat.STATE_ERROR,
        PlaybackStateCompat.STATE_BUFFERING, PlaybackStateCompat.STATE_CONNECTING, PlaybackStateCompat.STATE_SKIPPING_TO_NEXT,
        PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS, PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM -> false
        else -> true
    }
}