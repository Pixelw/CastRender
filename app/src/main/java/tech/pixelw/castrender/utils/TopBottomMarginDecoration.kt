package tech.pixelw.castrender.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TopBottomMarginDecoration() : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        parent.adapter?.itemCount?.let {
            val marginVert = (parent.height - view.height) / 2
            when (parent.getChildAdapterPosition(view)) {
                0 -> { // last or first one
                    outRect.set(0, marginVert, 0, 0)
                }
                it - 1 -> {
                    outRect.set(0, 0, 0, marginVert)
                }
            }
        }
    }

//    private fun getOffsetPixelSize(parent: RecyclerView, view: View): Int {
//        val orientationHelper = OrientationHelper.createVerticalHelper(parent.layoutManager)
//        return (orientationHelper.totalSpace - view.layoutParams.height) / 2
//    }
}