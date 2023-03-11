package tech.pixelw.castrender.feature.render.player

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.support.v4.media.session.PlaybackStateCompat
import android.view.Gravity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tech.pixelw.castrender.R
import tech.pixelw.castrender.utils.LogUtil
import tech.pixelw.castrender.utils.ktx.HandlerKtx.cancel
import tech.pixelw.castrender.utils.ktx.HandlerKtx.runOnMain
import tech.pixelw.castrender.utils.ktx.HandlerKtx.runOnMainDelayed
import tech.pixelw.castrender.utils.ktx.getString
import tech.pixelw.castrender.utils.ktx.toast
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MediaPlayerImplementation(private val coroutineScope: CoroutineScope) :
    IPlayer<MediaPlayerImplementation.PlayerWrapper>(), MediaPlayer.OnPreparedListener {

    private val mediaPlayer = PlayerWrapper()
    private var playWhenReady = false
    private var setDisplayJob: Job? = null

    private val pollTask = object : Runnable {
        override fun run() {
            position = mediaPlayer.currentPosition.toLong()
            if (ticks % 4 == 0) {
                duration = mediaPlayer.duration.toLong()
                speed = mediaPlayer.speed
            }
            runOnMainDelayed(positionPollInterval)
            ticks++
        }
    }

    init {
        mediaPlayer.setOnCompletionListener {
            mediaSessionState = PlaybackStateCompat.STATE_STOPPED
        }
        mediaPlayer.setOnPreparedListener(this)
    }

    override fun playerInstance(): PlayerWrapper {
        return mediaPlayer
    }

    override fun bindView(view: View) {
        if (view is SurfaceView) {
            val holder = view.holder
            setDisplayJob = coroutineScope.launch {
                LogUtil.e("115144", "dsadsadasdsa")
                suspendCoroutine<Unit> { co ->
                    holder.addCallback(object : SurfaceHolder.Callback {
                        override fun surfaceCreated(holder: SurfaceHolder) {
                            LogUtil.d(TAG, "surfaceCreated() called with: holder = $holder")
                            mediaPlayer.setDisplay(holder)
                            co.resume(Unit)
                        }

                        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                            LogUtil.d(
                                TAG,
                                "surfaceChanged() called with: holder = $holder, format = $format, width = $width, height = $height"
                            )
                        }

                        override fun surfaceDestroyed(holder: SurfaceHolder) {
                            LogUtil.d(TAG, "surfaceDestroyed() called with: holder = $holder")
                        }

                    })
                }
            }

            mediaPlayer.setOnVideoSizeChangedListener { _, videoWidth, videoHeight ->
                val screenWidth: Int = (view.context as Activity).windowManager.defaultDisplay.width
                val screenHeight: Int = (view.context as Activity).windowManager.defaultDisplay.height
                val layoutParams = view.layoutParams as FrameLayout.LayoutParams
                layoutParams.gravity = Gravity.CENTER

                if (videoWidth > videoHeight) {
                    layoutParams.width = screenWidth
                    layoutParams.height = screenWidth * videoHeight / videoWidth
                } else {
                    layoutParams.width = screenHeight * videoWidth / videoHeight
                    layoutParams.height = screenHeight
                }

                view.layoutParams = layoutParams
            }

        }
    }

    override fun prepareMedia(url: String, playWhenReady: Boolean) {
        coroutineScope.launch(Dispatchers.Main) {
            mediaPlayer.reset()
            setDisplayJob?.join()
            mediaSessionState = PlaybackStateCompat.STATE_NONE
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            this@MediaPlayerImplementation.playWhenReady = playWhenReady
        }

    }

    override fun play() {
        try {
            if (mediaPlayer.isPrepared.not()) {
                return
            }
            mediaPlayer.start()
            mediaSessionState = PlaybackStateCompat.STATE_PLAYING
            pollTask.runOnMain()
            callbacks.forEach { it.onIsPlayingChanged(true) }
        } catch (t: java.lang.IllegalStateException) {
            LogUtil.e(TAG, "can not play on this state", t)
        }
    }

    override fun pause() {
        try {
            mediaPlayer.pause()
            pollTask.cancel()
            callbacks.forEach { it.onIsPlayingChanged(false) }
            mediaSessionState = PlaybackStateCompat.STATE_PAUSED
        } catch (t: java.lang.IllegalStateException) {
            LogUtil.e(TAG, "can not pause on this state", t)
        }
    }

    override fun stop() {
        try {
            mediaPlayer.stop()
            mediaSessionState = PlaybackStateCompat.STATE_STOPPED
            pollTask.cancel()
        } catch (t: java.lang.IllegalStateException) {
            LogUtil.e(TAG, "can not stop on this state", t)
        }
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun close() {
        try {
            mediaPlayer.stop()
            mediaPlayer.release()
            mediaSessionState = PlaybackStateCompat.STATE_NONE
        } catch (t: java.lang.IllegalStateException) {
            LogUtil.e(TAG, "can not close on this state", t)
        }

    }

    override var speed
        get() = mediaPlayer.speed
        set(value) {
            mediaPlayer.speed = value
        }

    override fun seekTo(millis: Long) {
        try {
            mediaPlayer.seekTo(millis.toInt())
        } catch (t: java.lang.IllegalStateException) {
            LogUtil.e(TAG, "can not stop on this state", t)
        } catch (tx: Throwable) {
            LogUtil.e(TAG, "unknown error", tx)
        }
    }


    class PlayerWrapper : MediaPlayer() {
        var isPrepared = false
        var uri: Uri? = null
        var state = 0
        var speed = 1.0f
            set(value) {
                if (field != value) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        playbackParams = playbackParams.setSpeed(value)
                    } else {
                        toast(getString(R.string.unsupported_speed))
                    }
                    field = value
                }
            }
        var isDisplaySet = false

        init {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
        }

        override fun setDataSource(context: Context, uri: Uri) {
            super.setDataSource(context, uri)
            this.uri = uri
        }

        override fun reset() {
            super.reset()
            isPrepared = false
            uri = null

        }

        override fun setDisplay(sh: SurfaceHolder?) {
            super.setDisplay(sh)
            isDisplaySet = true
        }
    }

    companion object {
        private const val TAG = "MediaPlayerImplementati"
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mediaPlayer.isPrepared = true
        if (playWhenReady) {
            play()
        }
    }

}