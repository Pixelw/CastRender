package tech.pixelw.castrender.feature.mediainfo;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.StyleSpan;

import java.util.ArrayList;
import java.util.List;

import tech.pixelw.castrender.CastRenderApp;
import tech.pixelw.castrender.R;
import tech.pixelw.castrender.utils.SpanStringBuilder;

public class MediaInfo {
    private List<Track> tracks = new ArrayList<>();

    public List<Track> getTracks() {
        return tracks;
    }

    public static abstract class Track {
        public int trackId;
        public abstract CharSequence getDescription();
    }

    public static class Video extends Track {
        public Video(int trackId, int width, int height, float frameRate, String codec, float pixelAspectRatio) {
            this.trackId = trackId;
            this.width = width;
            this.height = height;
            this.frameRate = frameRate;
            this.codec = codec;
            this.pixelAspectRatio = pixelAspectRatio;
        }

        private final int width;
        private final int height;
        private final float frameRate;
        private final String codec;
        private final float pixelAspectRatio;

        public String getWidth() {
            return valueStrWithNullCheck(width);
        }

        public String getHeight() {
            return valueStrWithNullCheck(height);
        }

        public String getFrameRate() {
            return valueStrWithNullCheck(frameRate);
        }

        public String getCodec() {
            return valueStrWithNullCheck(codec);
        }

        public String getPixelAspectRatio() {
            return valueStrWithNullCheck(pixelAspectRatio);
        }

        public CharSequence getDescription() {
            int flag = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE;
            return new SpanStringBuilder()
                    .append("Codec\n", new StyleSpan(Typeface.BOLD), flag).append(getCodec()).append("\n")
                    .append("Resolution\n", new StyleSpan(Typeface.BOLD), flag).append(getWidth() + "x"  + getHeight()).append("\n")
                    .append("Frame Rate\n", new StyleSpan(Typeface.BOLD), flag).append(getFrameRate()).append("\n");

        }
    }

    public static class Audio extends Track {
        public Audio(int trackId, String codec, int channelCount, int samplingRate) {
            this.trackId = trackId;
            this.codec = codec;
            this.channelCount = channelCount;
            this.samplingRate = samplingRate;
        }

        private final String codec;
        private final int channelCount;
        private final int samplingRate;

        public String getCodec() {
            return valueStrWithNullCheck(codec);
        }

        public String getChannelCount() {
            return valueStrWithNullCheck(channelCount);
        }

        public String getSamplingRate() {
            return valueStrWithNullCheck(samplingRate);
        }

        public CharSequence getDescription() {
            int flag = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE;
            return new SpanStringBuilder()
                    .append("Codec\n", new StyleSpan(Typeface.BOLD), flag).append(getCodec()).append("\n")
                    .append("Channels\n", new StyleSpan(Typeface.BOLD), flag).append(getChannelCount()).append("\n")
                    .append("Sample Rate\n", new StyleSpan(Typeface.BOLD), flag).append(getSamplingRate());
        }
    }

    public static String valueStrWithNullCheck(Number number){
        if (number.intValue() < 0){
            return CastRenderApp.getAppContext().getString(R.string.unknown);
        }
        return String.valueOf(number);
    }

    public static String valueStrWithNullCheck(String string){
        if (TextUtils.isEmpty(string)) {
            return CastRenderApp.getAppContext().getString(R.string.unknown);
        }
        return string;
    }

}
