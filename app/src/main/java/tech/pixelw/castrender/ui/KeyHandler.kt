package tech.pixelw.castrender.ui

import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import org.fourthline.cling.support.model.TransportState
import tech.pixelw.castrender.utils.LogUtil

class KeyHandler constructor(private val player: ExoPlayer, private val callback: Callback) {

    private val TAG = "KeyHandler"
    private val handler: Handler = Handler(Looper.getMainLooper())

    private val longPressable = intArrayOf(KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER, KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT)
    private val notLongPressable = intArrayOf(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)

    private var originalSpeed = player.playbackParameters.speed
    private var isFastRewind = false
    private val isUsingRealFFWD = true
    private val fastJumpThread = object : Runnable {
        override fun run() {
            if (isFastRewind) {
                player.seekBack()
                handler.postDelayed(this, 300)
            } else {
                if (isUsingRealFFWD) {
                    player.setPlaybackSpeed(8.0f)
                } else {
                    player.seekForward()
                    handler.postDelayed(this, 300)
                }
            }
        }
    }

    private fun handleClick(keyCode: Int, event: KeyEvent): Boolean {
        LogUtil.d(TAG, "click$keyCode")
        if (player.playbackState != Player.STATE_READY) return true
        when (keyCode) {
            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE ->
                if (player.isPlaying) {
                    player.pause()
                    callback.onTransportStateChanged(TransportState.PAUSED_PLAYBACK)
                } else {
                    player.play()
                    callback.onTransportStateChanged(TransportState.PLAYING)
                }
            KeyEvent.KEYCODE_DPAD_LEFT -> player.seekBack()

            KeyEvent.KEYCODE_DPAD_RIGHT -> player.seekForward()
        }
        return true
    }

    private fun handleLongClick(keyCode: Int, event: KeyEvent): Boolean {
        LogUtil.d(TAG, "longClick$keyCode")
        if (player.playbackState != Player.STATE_READY) return true
        when (keyCode) {
            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER ->
                if (player.isPlaying) {
                    originalSpeed = player.playbackParameters.speed
                    player.setPlaybackSpeed(2.0f)
                }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                isFastRewind = true
                handler.post(fastJumpThread)
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                isFastRewind = false
                handler.post(fastJumpThread)
            }
        }
        return true
    }

    private fun handleLongClickRelease(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                player.setPlaybackSpeed(originalSpeed)
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> handler.removeCallbacks(fastJumpThread)
            KeyEvent.KEYCODE_DPAD_RIGHT -> if (isUsingRealFFWD) {
                player.setPlaybackSpeed(originalSpeed)
            } else {
                handler.removeCallbacks(fastJumpThread)
            }
        }
        return true
    }

    fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode in longPressable || keyCode in notLongPressable) {
            event.startTracking()
            return true
        }
        return false
    }

    fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode in longPressable) {
            return handleLongClick(keyCode, event)
        }
        return false;
    }

    fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        LogUtil.d(TAG, "0x${Integer.toHexString(event.flags)}")
        if (keyCode in longPressable || keyCode in notLongPressable) {
            return if (event.flags.and(KeyEvent.FLAG_CANCELED_LONG_PRESS) == 0) {
                handleClick(keyCode, event)
            } else {
                handleLongClickRelease(keyCode, event);
            }
        }
        return false
    }

    interface Callback {
        fun onTransportStateChanged(state: TransportState)
    }
}