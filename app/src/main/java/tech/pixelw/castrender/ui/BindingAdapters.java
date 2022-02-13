package tech.pixelw.castrender.ui;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import tech.pixelw.castrender.R;
import tech.pixelw.castrender.ui.mediainfo.MediaInfo;

/**
 * @author Carl Su "Pixelw"
 * @date 2021/10/15
 */
public class BindingAdapters {
    @BindingAdapter("mediaInfoIcon")
    public static void mediaInfoIcon(ImageView view, MediaInfo.Track track) {
        if (track instanceof MediaInfo.Video) {
            view.setImageResource(R.drawable.ic_baseline_movie_24);
        } else if (track instanceof MediaInfo.Audio) {
            view.setImageResource(R.drawable.ic_baseline_audiotrack_24);
        }
    }
}
