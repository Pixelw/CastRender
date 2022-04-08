package tech.pixelw.castrender.ui.render.music

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
            when (parent.getChildAdapterPosition(view)) {
                it - 1, 0 -> { // last or first one
                    val marginVert = (parent.height - view.height) / 2
                    outRect.set(0, marginVert, 0, marginVert)
                }
            }
        }
    }

//    private fun getOffsetPixelSize(parent: RecyclerView, view: View): Int {
//        val orientationHelper = OrientationHelper.createVerticalHelper(parent.layoutManager)
//        return (orientationHelper.totalSpace - view.layoutParams.height) / 2
//    }
}