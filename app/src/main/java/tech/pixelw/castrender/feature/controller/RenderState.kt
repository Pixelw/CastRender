package tech.pixelw.castrender.feature.controller

import androidx.annotation.DrawableRes
import org.fourthline.cling.support.model.MediaInfo
import org.fourthline.cling.support.model.PositionInfo
import org.fourthline.cling.support.model.TransportInfo
import org.fourthline.cling.support.model.TransportState
import tech.pixelw.castrender.R

data class RenderState(val deviceName: String) {
    var volume = 0
    var positionInfo: PositionInfo? = null
    var transportInfo: TransportInfo? = null
    var mediaInfo: MediaInfo? = null

    fun getSliderValue(): Float {
        return positionInfo?.elapsedPercent?.div(100.0f) ?: 0.0f
    }

    fun isPlaying(): Boolean {
        return transportInfo?.currentTransportState == TransportState.PLAYING
    }

    @DrawableRes
    fun playPauseDrawable(): Int {
        return if (isPlaying()) {
            R.drawable.exo_controls_pause
        } else {
            R.drawable.exo_controls_play
        }
    }

}