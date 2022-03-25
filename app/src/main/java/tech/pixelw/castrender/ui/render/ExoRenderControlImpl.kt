package tech.pixelw.castrender.ui.render

import android.os.Handler
import android.os.Looper
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import org.fourthline.cling.support.model.TransportState
import tech.pixelw.castrender.entity.MediaEntity
import tech.pixelw.castrender.utils.LogUtil
import tech.pixelw.dmr_core.IDLNARenderControl

/**
 * All called in workers thread
 *
 * @author Carl Su "Pixelw"
 * @date 2021/12/6
 */
class ExoRenderControlImpl(
    private val player: ExoPlayer?,
    private val activityCallback: ActivityCallback
) : IDLNARenderControl {

    private var upnpMediaInfo: String? = null
    private val handler = Handler(Looper.getMainLooper())

    @Volatile
    private var duration: Long = 0

    @Volatile
    private var position: Long = 0
    private var speed = 0f
    private var transportState = TransportState.NO_MEDIA_PRESENT
    private val refreshThread: Runnable = object : Runnable {
        override fun run() {
            if (player == null) return
            position = player.currentPosition
            duration = player.duration
            if (player.isPlaying || player.playbackState == Player.STATE_BUFFERING) {
                handler.postDelayed(this, UPDATE_INTERVAL)
            }
        }
    }

    override fun type(): Int {
        return TYPE
    }

    override fun prepare(uri: String) {
        handler.post {
            val mediaItem = MediaItem.fromUri(uri)
            player!!.setMediaItem(mediaItem)
            duration = 0
            position = duration
            player.prepare()
        }
    }

    override fun play() {
        handler.post {
            LogUtil.i(TAG, "play called")
            player!!.play()
        }
    }

    override fun pause() {
        handler.post {
            LogUtil.i(TAG, "pause called")
            player!!.pause()
        }
    }

    override fun seek(position: Long) {
        handler.post {
            LogUtil.i(TAG, "seekTo: $position")
            if (position < 3000 && Math.abs(player!!.currentPosition - position) < 1000) {
                LogUtil.w(TAG, "ignored just start seek")
                return@post
            }
            player!!.seekTo(position)
        }
    }

    override fun stop() {
        handler.post {
            LogUtil.i(TAG, "stop called")
            player!!.stop()
            activityCallback.finishPlayer()
        }
    }

    override fun getPosition(): Long {
        LogUtil.d(TAG, "getPosition=$position")
        return position
    }

    override fun getDuration(): Long {
        LogUtil.d(TAG, "getDuration=$duration")
        return duration
    }

    override fun getTransportState(): TransportState {
        LogUtil.d(TAG, "getState=" + transportState.name)
        return transportState
    }

    override fun setMediaInfo(metadata: String) {
        upnpMediaInfo = metadata
        MediaEntity.parseFromDIDL(metadata)?.let { activityCallback.setMediaEntity(it) }
    }

    override fun getSpeed(): Float {
        return speed
    }

    interface ActivityCallback {
        fun finishPlayer()
        fun setMediaEntity(mediaEntity: MediaEntity)
    }

    companion object {
        private const val TAG = "ExoplayerRenderControl"
        const val TYPE = 101
        const val UPDATE_INTERVAL = 300L
    }

    init {
        // call this first
        player!!.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_IDLE -> transportState = TransportState.NO_MEDIA_PRESENT
                    Player.STATE_BUFFERING, Player.STATE_READY -> {}
                    Player.STATE_ENDED -> {
                        transportState = TransportState.STOPPED
                        position = duration // fix inconsistency
                    }
                }
            }

            // then call this
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (player.playbackState == Player.STATE_READY) {
                    transportState = if (isPlaying) {
                        TransportState.PLAYING
                    } else {
                        TransportState.PAUSED_PLAYBACK
                    }
                }
                handler.removeCallbacks(refreshThread)
                handler.postDelayed(refreshThread, 100)
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                speed = playbackParameters.speed
            }
        })
    }
}