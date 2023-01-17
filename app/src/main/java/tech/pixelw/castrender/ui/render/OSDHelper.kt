package tech.pixelw.castrender.ui.render

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.Insets
import com.google.android.material.progressindicator.LinearProgressIndicator
import tech.pixelw.castrender.R
import tech.pixelw.castrender.utils.LogUtil
import tech.pixelw.castrender.utils.SafeZoneHelper
import tech.pixelw.castrender.utils.TimeUtil
import kotlin.math.max

/**
 * 管理播放器上的OSD
 */
class OSDHelper(private val safeZone: View) {

    private var llSeekOsd: LinearLayout = safeZone.findViewById(R.id.ll_osd_seek)
    private var tvSeekTime: TextView = safeZone.findViewById(R.id.tv_seek_time)
    private var progressBar: LinearProgressIndicator = safeZone.findViewById(R.id.progress_seek)
    private var llFFWD: LinearLayout = safeZone.findViewById(R.id.ll_osd_ffwd)
    private var tvFFWD: TextView = safeZone.findViewById(R.id.tv_fast_forward)

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        llSeekOsd.visibility = View.INVISIBLE
    }

    init {
        SafeZoneHelper.observe(safeZone, true, true) { insets: Insets ->
            LogUtil.i("SafeZone", "SafeZone in pixels $insets")
            val lp = safeZone.layoutParams as ViewGroup.MarginLayoutParams
            val horizontal = max(
                max(insets.left, insets.right),
                max(lp.leftMargin, lp.rightMargin)
            )
            val top = max(lp.topMargin, insets.top)
            val bottom = max(lp.bottomMargin, insets.bottom)
            lp.setMargins(horizontal, top, horizontal, bottom)
        }

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
        tvFFWD.text = speed.toString() + safeZone.context.getString(R.string.xspeed_skip)
    }


}