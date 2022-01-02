package tech.pixelw.castrender.ui;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;

import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.support.renderingcontrol.lastchange.ChannelVolume;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlVariable;

import tech.pixelw.castrender.R;
import tech.pixelw.castrender.databinding.ActivityPlayer2Binding;
import tech.pixelw.dmr_core.DLNARendererService;
import tech.pixelw.dmr_core.service.AVTransportController;

public class PlayerActivity extends AppCompatActivity {

    public static final String EXTRA_KEY_URL = "EXTRA_KEY_MEDIA_URL";
    // TODO: 2021/10/25 ??
    private ActivityPlayer2Binding binding;
    private ExoPlayer exoPlayer;
    private DLNARendererService.RendererServiceBinder binder;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (DLNARendererService.RendererServiceBinder) service;
            binder.registerController(avTransportController);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    };
    private AVTransportController avTransportController;


    public static void newPlayerInstance(Context context, String url) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(EXTRA_KEY_URL, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player2);
        exoPlayer = new SimpleExoPlayer.Builder(this).build();
        binding.exoPlayerView.setPlayer(exoPlayer);
        connectToService();
        onNewIntent(getIntent());
    }

    private void connectToService() {
        avTransportController = new AVTransportController(PlayerActivity.this, new ExoRenderControlImpl(exoPlayer));
        Intent intent = new Intent(this, DLNARendererService.class);
        intent.putExtra("foreground", true);
        bindService(intent, connection, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent == null) return;
        super.onNewIntent(intent);
        String url = intent.getStringExtra(EXTRA_KEY_URL);
        if (!TextUtils.isEmpty(url)) {
            prepareAndPlayMedia(url);
        }
    }

    private void prepareAndPlayMedia(String url) {
        runOnUiThread(() -> {
            MediaItem mediaItem = MediaItem.fromUri(url);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        exoPlayer.pause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = super.onKeyDown(keyCode, event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (exoPlayer == null) return handled;
                if (exoPlayer.isPlaying()) {
                    exoPlayer.pause();
                    notifyTransportStateChanged(TransportState.PAUSED_PLAYBACK);
                } else {
                    exoPlayer.play();
                    notifyTransportStateChanged(TransportState.PLAYING);
                }
                break;
            default:
                break;
        }
        return handled;
    }

    private void notifyTransportStateChanged(TransportState state) {
        if (binder != null) {
            binder.avTransportLastChange()
                    .setEventedValue(avTransportController.getInstanceId(),
                            new AVTransportVariable.TransportState(state));
        }
    }

    private void notifyRenderVolumeChanged(int volume) {
        if (binder != null) {
            binder.audioControlLastChange().
                    setEventedValue(avTransportController.getInstanceId(),
                            new RenderingControlVariable.Volume(
                                    new ChannelVolume(Channel.Master, volume)));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exoPlayer.stop();
        notifyTransportStateChanged(TransportState.STOPPED);
        exoPlayer.release();
        binder.unregisterController(avTransportController);
        unbindService(connection);
    }

    public final class Handler {
        public void navigationClick(View view) {
            finish();
        }
    }
}