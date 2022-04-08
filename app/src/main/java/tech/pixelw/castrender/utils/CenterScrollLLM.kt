package tech.pixelw.castrender.utils

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class CenterScrollLLM(val context: Context) : LinearLayoutManager(context) {
    override fun smoothScrollToPosition(
        recyclerView: RecyclerView?,
        state: RecyclerView.State?,
        position: Int
    ) {
        recyclerView?.context?.let {
            CenterSmoothScroller(it).run {
                targetPosition = position
                startSmoothScroll(this)
            }
        }
    }

    class CenterSmoothScroller(context: Context) : LinearSmoothScroller(context) {
        companion object {
            const val MILLIS_PER_INCH = 100f
        }

        override fun calculateDtToFit(
            viewStart: Int,
            viewEnd: Int,
            boxStart: Int,
            boxEnd: Int,
            snapPreference: Int
        ): Int {
            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
        }

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
            displayMetrics?.densityDpi?.let {
                return MILLIS_PER_INCH / it
            }
            return super.calculateSpeedPerPixel(displayMetrics)
        }
    }
}