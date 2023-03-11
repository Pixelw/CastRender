package tech.pixelw.castrender.feature.render

import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import tech.pixelw.castrender.feature.render.player.IPlayer
import tech.pixelw.castrender.feature.render.player.isReady
import tech.pixelw.castrender.utils.LogUtil

class KeyHandler constructor(private val player: IPlayer<*>) {

    private val TAG = "KeyHandler"
    private val handler: Handler = Handler(Looper.getMainLooper())

    private val longPressable = intArrayOf(
        KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_NUMPAD_ENTER, KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT
    )
    private val notLongPressable = intArrayOf(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)

    private var originalSpeed = player.speed
    private var isFastRewind = false
    private val fastJumpThread = object : Runnable {
        override fun run() {
            if (isFastRewind) {
                player.seekBack()
            } else {
                player.seekForward()
            }
            osdHelper.setSeekOsd(true, player.position, player.duration)
            handler.postDelayed(this, 300)
        }
    }
    private lateinit var osdHelper: OSDHelper

    private fun handleClick(keyCode: Int, event: KeyEvent): Boolean {
        LogUtil.d(TAG, "click$keyCode")
        if (!player.mediaSessionState.isReady()) return true
        when (keyCode) {
            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, KeyEvent.KEYCODE_SPACE ->
                if (player.isPlaying()) {
                    player.pause()
                } else {
                    player.play()
                }
            KeyEvent.KEYCODE_DPAD_LEFT -> player.seekBack()

            KeyEvent.KEYCODE_DPAD_RIGHT -> player.seekForward()
        }
        return true
    }

    private fun handleLongClick(keyCode: Int, event: KeyEvent): Boolean {
        LogUtil.d(TAG, "longClick$keyCode")
        if (!player.mediaSessionState.isReady()) return true
        when (keyCode) {
            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER, KeyEvent.KEYCODE_SPACE ->
                if (player.isPlaying()) {
                    originalSpeed = player.speed
                    player.speed = 2.0f
                    osdHelper.setQuickFastForwardOsd(true, 2.0f)
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
                player.speed = originalSpeed
                osdHelper.setQuickFastForwardOsd(false)
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> handler.removeCallbacks(fastJumpThread)
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                handler.removeCallbacks(fastJumpThread)
            }
        }
        return true
    }

    fun attachOsd(osdHelper: OSDHelper) {
        this.osdHelper = osdHelper
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
}