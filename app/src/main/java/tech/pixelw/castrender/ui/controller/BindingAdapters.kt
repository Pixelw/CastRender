package tech.pixelw.castrender.ui.controller

import android.annotation.SuppressLint
import androidx.databinding.BindingAdapter
import com.google.android.material.slider.Slider

object BindingAdapters {
    @BindingAdapter("sliderCustomListener")
    @SuppressLint("RestrictedApi")
    @JvmStatic
    fun setOnValueChangedListener(
        slider: Slider,
        onSlideChangedListener: OnSlideChangedListener
    ) {
        class SlideCusListener : Slider.OnChangeListener, Slider.OnSliderTouchListener {
            var value = 0f

            override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
                if (fromUser) this.value = value
            }

            override fun onStartTrackingTouch(slider: Slider) {
                onSlideChangedListener.onChanged(USER_START_DRAGGING)
            }

            override fun onStopTrackingTouch(slider: Slider) {
                onSlideChangedListener.onChanged(value)
            }
        }

        val slideCusListener = SlideCusListener()
        slider.addOnChangeListener(slideCusListener)
        slider.addOnSliderTouchListener(slideCusListener)
    }

    const val USER_START_DRAGGING = -1.11f

    interface OnSlideChangedListener {
        fun onChanged(v: Float)
    }
}
