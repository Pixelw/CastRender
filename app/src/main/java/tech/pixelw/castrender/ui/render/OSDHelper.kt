package tech.pixelw.castrender.ui.render

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.progressindicator.LinearProgressIndicator
import tech.pixelw.castrender.R
import tech.pixelw.castrender.utils.TimeUtil

/**
 * 管理播放器上的OSD
 */
class OSDHelper constructor(private val osdSafeZone: View) {

    private var llSeekOsd: LinearLayout = osdSafeZone.findViewById(R.id.ll_osd_seek)
    private var tvSeekTime: TextView = osdSafeZone.findViewById(R.id.tv_seek_time)
    private var progressBar: LinearProgressIndicator = osdSafeZone.findViewById(R.id.progress_seek)
    private var llFFWD: LinearLayout = osdSafeZone.findViewById(R.id.ll_osd_ffwd)
    private var tvFFWD: TextView = osdSafeZone.findViewById(R.id.tv_fast_forward)

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        llSeekOsd.visibility = View.INVISIBLE
    }

    @SuppressLint("SetTextI18n")
    fun setSeekOsd(visible: Boolean = true, position: Long = 0, duration: Long = 1) {
        llSeekOsd.visibility = if (visible) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }

        tvSeekTime.text = TimeUtil.toTimeString(position) + "/" + TimeUtil.toTimeString(duration)
        val value = position.toFloat() / duration.toFloat() * 100
        progressBar.setProgress(value.toInt())
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, 2000)
    }

    fun setQuickFastForwardOsd(visible: Boolean, speed: Float = 2.0f) {
        llFFWD.visibility = if (visible) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
        tvFFWD.text = speed.toString() + osdSafeZone.context.getString(R.string.xspeed_skip)
    }


}