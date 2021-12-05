package tech.pixelw.castrender.ui;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;

import tech.pixelw.castrender.utils.LogUtil;
import tech.pixelw.dmr_core.IDLNARenderControl;

/**
 * @author Carl Su "Pixelw"
 * @date 2021/12/6
 */
public class ExoRenderControlImpl implements IDLNARenderControl {
    private final SimpleExoPlayer player;
    public ExoRenderControlImpl(SimpleExoPlayer player) {
        this.player = player;
    }

    private static final String TAG = "ExoplayerRenderControl";

    @Override
    public void prepare(String uri) {
        // TODO: 2021/12/6 why got not playing
        LogUtil.i(TAG, "got uri:" + uri);
        MediaItem mediaItem = MediaItem.fromUri(uri);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    @Override
    public void play() {
        player.play();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public void seek(long position) {
        player.seekTo(position);
    }

    @Override
    public void stop() {
        player.stop();
    }

    @Override
    public long getPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return player.getDuration();
    }
}
