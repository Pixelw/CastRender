package tech.pixelw.castrender.ui;

import android.os.Handler;
import android.os.Looper;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

import org.fourthline.cling.support.model.TransportState;

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
    public static final int TYPE = 101;

    private final ExoPlayer player;

    public ExoRenderControlImpl(ExoPlayer player) {
        this.player = player;
        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                handler.removeCallbacks(refreshThread);
                handler.post(refreshThread);
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE){
                    position = duration = 0;
                }
            }
        });
    }

    private final Handler handler = new Handler(Looper.getMainLooper());

    private volatile long duration, position;
    private float speed;
    private TransportState transportState = TransportState.NO_MEDIA_PRESENT;

    public static final long UPDATE_INTERVAL = 300L;
    private final Runnable refreshThread = new Runnable() {
        @Override
        public void run() {
            if (player == null) return;
            position = player.getCurrentPosition();
            duration = player.getDuration();
            switch (player.getPlaybackState()) {
                case com.google.android.exoplayer2.Player.STATE_IDLE:
                    transportState = TransportState.NO_MEDIA_PRESENT;
                    break;
                case com.google.android.exoplayer2.Player.STATE_ENDED:
                    transportState = TransportState.STOPPED;
                    break;
                case com.google.android.exoplayer2.Player.STATE_BUFFERING:
                    transportState = TransportState.PLAYING;
                    break;
                case com.google.android.exoplayer2.Player.STATE_READY:
                    if (player.isPlaying()) {
                        transportState = TransportState.PLAYING;
                    } else {
                        transportState = TransportState.PAUSED_PLAYBACK;
                    }
                    break;
            }
            speed = player.getPlaybackParameters().speed;
            if (player.isPlaying() || player.getPlaybackState() == Player.STATE_BUFFERING) {
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        }
    };

    @Override
    public int type() {
        return TYPE;
    }

    @Override
    public void prepare(String uri) {
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
        });
    }

    @Override
    public void pause() {
        handler.post(() -> {
            LogUtil.i(TAG, "pause called");
            player.pause();
        });
    }

    @Override
    public void seek(long position) {
        handler.post(() -> {
            LogUtil.i(TAG, "seekTo: " + position);
            player.seekTo(position);
        });
    }

    @Override
    public void stop() {
        handler.post(() -> {
            LogUtil.i(TAG, "stop called");
            player.stop();
        });
    }

    @Override
    public long getPosition() {
        LogUtil.d(TAG, "getPosition=" + position);
        return position;
    }

    @Override
    public long getDuration() {
        LogUtil.d(TAG, "getDuration=" + duration);
        return duration;
    }

    @Override
    public TransportState getTransportState() {
        LogUtil.d(TAG, "getState=" + transportState.name());
        return transportState;
    }

    @Override
    public float getSpeed() {
        return speed;
    }
}
