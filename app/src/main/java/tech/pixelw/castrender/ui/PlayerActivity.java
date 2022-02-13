package tech.pixelw.castrender.ui;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.BaseInputConnection;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.support.renderingcontrol.lastchange.ChannelVolume;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlVariable;

import tech.pixelw.castrender.R;
import tech.pixelw.castrender.databinding.ActivityPlayer2Binding;
import tech.pixelw.castrender.ui.mediainfo.MediaInfoListAdapter;
import tech.pixelw.castrender.ui.mediainfo.MediaInfoRetriever;
import tech.pixelw.castrender.utils.LogUtil;
import tech.pixelw.castrender.utils.SafeZoneHelper;
import tech.pixelw.dmr_core.DLNARendererService;

public class PlayerActivity extends AppCompatActivity implements ExoRenderControlImpl.ActivityCallback {

    public static final String EXTRA_KEY_URL = "EXTRA_KEY_MEDIA_URL";
    private ActivityPlayer2Binding binding;
    private SimpleExoPlayer exoPlayer;
    private DLNARendererService.RendererServiceBinder binder;
    private static final String TAG = "PlayerActivity";

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (DLNARendererService.RendererServiceBinder) service;
            binder.registerController(new ExoRenderControlImpl(exoPlayer, PlayerActivity.this));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    };
    private WindowInsetsControllerCompat controllerCompat;
    private ViewGroup safeZone;
    private MediaInfoRetriever retriever;

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
        controllerCompat = new WindowInsetsControllerCompat(getWindow(), binding.exoPlayerView);
        Toolbar toolbar = binding.exoPlayerView.findViewById(R.id.player_toolbar);
        setSupportActionBar(toolbar);
        safeZone = binding.exoPlayerView.findViewById(R.id.cl_controller_safe_zone);
        if (safeZone != null) {
            SafeZoneHelper.observe(safeZone, true, true, insets -> {
                LogUtil.i(TAG, "SafeZone in pixels " + insets);
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) safeZone.getLayoutParams();
                int horizontal = Math.max(Math.max(insets.left, insets.right), Math.max(lp.leftMargin, lp.rightMargin));
                int top = Math.max(lp.topMargin, insets.top);
                int bottom = Math.max(lp.bottomMargin, insets.bottom);
                lp.setMargins(horizontal, top, horizontal, bottom);
            });
        }
        exoPlayer = new SimpleExoPlayer.Builder(this).build();
        exoPlayer.setAudioAttributes(
                new AudioAttributes.Builder().setUsage(C.USAGE_MEDIA)
                        .setContentType(C.CONTENT_TYPE_MOVIE).build(),
                true);
        binding.exoPlayerView.setPlayer(exoPlayer);
        binding.setHandler(new Handler());
        retriever = new MediaInfoRetriever(exoPlayer);
        connectToService();
        onNewIntent(getIntent());
    }

    private void connectToService() {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_media_info) {
            View view = getLayoutInflater().inflate(R.layout.layout_media_info, null);
            RecyclerView recyclerView = view.findViewById(R.id.list_media_info);
            if (recyclerView != null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                // TODO: 2022/2/10 list
                MediaInfoListAdapter adapter = new MediaInfoListAdapter();
                adapter.setTrackList(retriever.mediaInfo.getTracks());
                recyclerView.setAdapter(adapter);
                AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                        .setTitle("MediaInfo")
                        .setView(view)
                        .create();
                dialog.show();
            }

        } else if (item.getItemId() == R.id.menu_speed_settings) {

        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        controllerCompat.hide(WindowInsetsCompat.Type.systemBars());
        controllerCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
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
                    .setEventedValue(binder.getInstanceId(),
                            new AVTransportVariable.TransportState(state));
        }
    }

    private void notifyRenderVolumeChanged(int volume) {
        if (binder != null) {
            binder.audioControlLastChange().
                    setEventedValue(binder.getInstanceId(),
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
        binder.unregisterController();
        unbindService(connection);
    }

    @Override
    public void finishPlayer() {
        finish();
    }

    public final class Handler {
        public void navigationClick(View view) {
            finish();
        }
    }

}