package tech.pixelw.castrender.ui;

import android.os.Handler;
import android.os.Looper;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

import tech.pixelw.castrender.utils.LogUtil;
import tech.pixelw.dmr_core.IDLNARenderControl;

/**
 * All called in workers thread
 *
 * @author Carl Su "Pixelw"
 * @date 2021/12/6
 */
public class ExoRenderControlImpl implements IDLNARenderControl {
    private static final String TAG = "ExoplayerRenderControl";
    private final ExoPlayer player;

    public ExoRenderControlImpl(ExoPlayer player) {
        this.player = player;
    }

    private final Handler handler = new Handler(Looper.getMainLooper());
    private volatile long duration, position;
    public static final long UPDATE_INTERVAL = 500L;
    private final Runnable refreshThread = new Runnable() {
        @Override
        public void run() {
            if (player == null) return;
            position = player.getCurrentPosition();
            duration = player.getDuration();
            handler.postDelayed(this, UPDATE_INTERVAL);
        }
    };

    @Override
    public int type() {
        return 101;
    }

    @Override
    public void prepare(String uri) {
        LogUtil.i(TAG, "got uri:" + uri);
        handler.post(() -> {
            MediaItem mediaItem = MediaItem.fromUri(uri);
            player.setMediaItem(mediaItem);
            player.prepare();
        });
    }

    @Override
    public void play() {
        handler.post(() -> {
            LogUtil.i(TAG, "play called");
            player.play();
            handler.removeCallbacks(refreshThread);
            handler.post(refreshThread);
        });
    }

    @Override
    public void pause() {
        handler.post(() -> {
            LogUtil.i(TAG, "pause called");
            player.pause();
            handler.removeCallbacks(refreshThread);
        });
    }

    @Override
    public void seek(long position) {
        handler.post(() -> player.seekTo(position));
    }

    @Override
    public void stop() {
        handler.post(() -> {
            LogUtil.i(TAG, "stop called");
            player.stop();
            handler.removeCallbacks(refreshThread);
        });
    }

    @Override
    public long getPosition() {
        LogUtil.i(TAG, "getPosition=" + position);
        return position;
    }

    @Override
    public long getDuration() {
        LogUtil.i(TAG, "getDuration=" + duration);
        return duration;
    }
}
