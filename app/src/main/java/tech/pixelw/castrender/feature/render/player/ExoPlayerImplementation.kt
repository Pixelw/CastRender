package tech.pixelw.castrender.feature.render.player

import android.content.Context
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ui.StyledPlayerView
import tech.pixelw.castrender.utils.ktx.HandlerKtx.cancel
import tech.pixelw.castrender.utils.ktx.HandlerKtx.runOnMainDelayed

class ExoPlayerImplementation(context: Context, audioType: Int = C.AUDIO_CONTENT_TYPE_MOVIE) : IPlayer<ExoPlayer>() {

    private val exoPlayer = ExoPlayer.Builder(
        context,
        DefaultRenderersFactory(context).setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
    ).setSeekForwardIncrementMs(5000)
        .setSeekBackIncrementMs(5000)
        .setAudioAttributes(
            AudioAttributes.Builder().setUsage(C.USAGE_MEDIA)
                .setContentType(audioType).build(), true
        )
        .build()

    private var playerView: StyledPlayerView? = null

    private val pollTask = object : Runnable {
        override fun run() {
            position = exoPlayer.currentPosition
            runOnMainDelayed(positionPollInterval)
        }
    }

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                mediaSessionState = getMediaSessionPlaybackState(playbackState, exoPlayer.playWhenReady)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playerView?.keepScreenOn = isPlaying
                if (isPlaying) {
                    pollTask.runOnMainDelayed(positionPollInterval)
                } else {
                    pollTask.cancel()
                }
                callbacks.forEach { it.onIsPlayingChanged(isPlaying) }
            }
        })
    }

    override fun bindView(view: View) {
        playerView = view as StyledPlayerView
        playerView?.player = exoPlayer
    }

    override var speed: Float = 1.0f

    override fun pause() {
        exoPlayer.pause()
    }

    override fun stop() {
        exoPlayer.stop()
    }

    override fun close() {
        exoPlayer.release()
        pollTask.cancel()
    }

    override fun seekTo(millis: Long) {
        exoPlayer.seekTo(position)
    }

    companion object {
        private const val TAG = "ExoPlayerImpl"
    }

    override fun playerInstance() = exoPlayer

    override fun prepareMedia(url: String, playWhenReady: Boolean) {
        val item = MediaItem.fromUri(url)
        exoPlayer.setMediaItem(item)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun play() {
        exoPlayer.play()
    }

    override fun getDuration(): Long {
        return exoPlayer.duration
    }

    override fun isPlaying(): Boolean {
        return exoPlayer.isPlaying
    }

    private fun getMediaSessionPlaybackState(exoPlayerPlaybackState: @Player.State Int, playWhenReady: Boolean): Int {
        val mapIdleToStopped = false
        return when (exoPlayerPlaybackState) {
            Player.STATE_BUFFERING -> if (playWhenReady) PlaybackStateCompat.STATE_BUFFERING else PlaybackStateCompat.STATE_PAUSED
            Player.STATE_READY -> if (playWhenReady) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
            Player.STATE_ENDED -> PlaybackStateCompat.STATE_STOPPED
            Player.STATE_IDLE -> if (mapIdleToStopped) PlaybackStateCompat.STATE_STOPPED else PlaybackStateCompat.STATE_NONE
            else -> if (mapIdleToStopped) PlaybackStateCompat.STATE_STOPPED else PlaybackStateCompat.STATE_NONE
        }
    }

}