package tech.pixelw.castrender.ui.render;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
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
    private final ActivityCallback activityCallback;

    public ExoRenderControlImpl(ExoPlayer player, ActivityCallback callback) {
        this.player = player;
        activityCallback = callback;
        // call this first
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case com.google.android.exoplayer2.Player.STATE_IDLE:   // 1
                        transportState = TransportState.NO_MEDIA_PRESENT;
                        break;
                    case com.google.android.exoplayer2.Player.STATE_BUFFERING:  // 2
                    case com.google.android.exoplayer2.Player.STATE_READY:      // 3
                        break;
                    case com.google.android.exoplayer2.Player.STATE_ENDED:  // 4
                        transportState = TransportState.STOPPED;
                        position = duration; // fix inconsistency
                        break;
                }
            }

            // then call this
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (player.getPlaybackState() == Player.STATE_READY) {
                    if (isPlaying) {
                        transportState = TransportState.PLAYING;
                    } else {
                        transportState = TransportState.PAUSED_PLAYBACK;
                    }
                }
                handler.removeCallbacks(refreshThread);
                handler.postDelayed(refreshThread, 100);
            }

            @Override
            public void onPlaybackParametersChanged(@NonNull PlaybackParameters playbackParameters) {
                speed = playbackParameters.speed;
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
            position = duration = 0;
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
            if (position < 3000 && Math.abs(player.getCurrentPosition() - position) < 1000) {
                LogUtil.w(TAG, "ignored just start seek");
                return;
            }
            player.seekTo(position);
        });
    }

    @Override
    public void stop() {
        handler.post(() -> {
            LogUtil.i(TAG, "stop called");
            player.stop();
            activityCallback.finishPlayer();
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

    interface ActivityCallback {
        void finishPlayer();
    }
}
