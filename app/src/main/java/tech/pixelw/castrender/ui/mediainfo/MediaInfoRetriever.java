package tech.pixelw.castrender.ui.mediainfo;

import android.text.TextUtils;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

public class MediaInfoRetriever implements Player.Listener {
    private static final String TAG = "MediaInfoRetriever";
    private final ExoPlayer player;
    public MediaInfo mediaInfo = new MediaInfo();

    /**
     * 要在载入媒体前初始化
     *
     * @param player Exoplayer
     */
    public MediaInfoRetriever(ExoPlayer player) {
        this.player = player;
        player.addListener(this);
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        int count = 0;
        for (int i = 0; i < trackGroups.length; i++) {
            TrackGroup trackGroup = trackGroups.get(i);
            for (int f = 0; f < trackGroup.length; f++) {
                Format format = trackGroup.getFormat(f);
                if (!TextUtils.isEmpty(format.sampleMimeType)) {
                    count++;
                    if (format.sampleMimeType.startsWith("audio")) {
                        MediaInfo.Audio audio = new MediaInfo.Audio(count, format.codecs, format.channelCount, format.sampleRate);
                        mediaInfo.getTracks().add(audio);
                    } else if (format.sampleMimeType.startsWith("video")) {
                        MediaInfo.Video video = new MediaInfo.Video(count, format.width, format.height, format.frameRate, format.codecs, format.pixelWidthHeightRatio);
                        mediaInfo.getTracks().add(video);
                    }
                }
            }

        }
    }
}
