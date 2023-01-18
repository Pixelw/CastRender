package tech.pixelw.castrender.utils

import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.slider.Slider
import tech.pixelw.castrender.R
import tech.pixelw.castrender.feature.mediainfo.MediaInfo

/**
 * @author Carl Su "Pixelw"
 * @date 2021/10/15
 */
object BindingAdapters {
    @BindingAdapter("mediaInfoIcon")
    fun mediaInfoIcon(view: ImageView, track: MediaInfo.Track?) {
        if (track is MediaInfo.Video) {
            view.setImageResource(R.drawable.ic_baseline_movie_24)
        } else if (track is MediaInfo.Audio) {
            view.setImageResource(R.drawable.ic_baseline_audiotrack_24)
        }
    }

    @BindingAdapter("imageSrc")
    fun imageSrc(view: ImageView?, url: String?) {
        if (view != null) {
            Glide.with(view).load(url).into(view)
        }
    }

    @BindingAdapter("sliderCustomListener")
    @SuppressLint("RestrictedApi")
    @JvmStatic
    fun setOnValueChangedListener(
        slider: Slider,
        onSlideChangedListener: OnSlideChangedListener
    ) {
        val slideCusListener = object : Slider.OnChangeListener, Slider.OnSliderTouchListener {
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

        slider.addOnChangeListener(slideCusListener)
        slider.addOnSliderTouchListener(slideCusListener)
    }

    const val USER_START_DRAGGING = -1.11f

    interface OnSlideChangedListener {
        fun onChanged(v: Float)
    }

}