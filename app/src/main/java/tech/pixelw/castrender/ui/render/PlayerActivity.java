package tech.pixelw.castrender.ui.render;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.fourthline.cling.support.model.TransportState;

import tech.pixelw.castrender.K;
import tech.pixelw.castrender.R;
import tech.pixelw.castrender.databinding.ActivityPlayer2Binding;
import tech.pixelw.castrender.ui.mediainfo.MediaInfoListAdapter;
import tech.pixelw.castrender.ui.mediainfo.MediaInfoRetriever;
import tech.pixelw.castrender.utils.LogUtil;
import tech.pixelw.castrender.utils.SafeZoneHelper;
import tech.pixelw.cling_common.entity.MediaEntity;
import tech.pixelw.dmr_core.DLNARendererService;

public class PlayerActivity extends AppCompatActivity implements ExoRenderControlImpl.ActivityCallback {


    private ActivityPlayer2Binding binding;
    private SimpleExoPlayer exoPlayer;
    private DLNARendererService binder;
    private static final String TAG = "PlayerActivity";

    private KeyHandler keyHandler;
    private OSDHelper osdHelper;

    private WindowInsetsControllerCompat controllerCompat;
    private ViewGroup safeZone;
    private MediaInfoRetriever retriever;
    private TransportState lastTransState = TransportState.CUSTOM;

    public static void newPlayerInstance(Context context, String url, @Nullable MediaEntity entity) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(K.EXTRA_KEY_URL, url);
        intent.putExtra(K.EXTRA_KEY_META, entity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player2);
        controllerCompat = ViewCompat.getWindowInsetsController(binding.exoPlayerView);
        Toolbar toolbar = binding.exoPlayerView.findViewById(R.id.player_toolbar);
        setSupportActionBar(toolbar);
        safeZone = binding.exoPlayerView.findViewById(R.id.cl_controller_safe_zone);
        if (safeZone != null) {
            SafeZoneHelper.observe(safeZone, true, true, insets -> {
                LogUtil.i(TAG, "SafeZone in pixels " + insets);
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) safeZone.getLayoutParams();
                ViewGroup.MarginLayoutParams lpOsd = (ViewGroup.MarginLayoutParams) binding.frOsdSafeZone.getLayoutParams();
                int horizontal = Math.max(Math.max(insets.left, insets.right), Math.max(lp.leftMargin, lp.rightMargin));
                int top = Math.max(lp.topMargin, insets.top);
                int bottom = Math.max(lp.bottomMargin, insets.bottom);
                lp.setMargins(horizontal, top, horizontal, bottom);
                lpOsd.setMargins(horizontal, top, horizontal, bottom);
            });
        }
        exoPlayer = new SimpleExoPlayer.Builder(this)
                .setSeekForwardIncrementMs(5000)
                .setSeekBackIncrementMs(5000)
                .setAudioAttributes(new AudioAttributes.Builder().setUsage(C.USAGE_MEDIA)
                        .setContentType(C.CONTENT_TYPE_MOVIE).build(), true)
                .build();
        binding.exoPlayerView.setPlayer(exoPlayer);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                binding.exoPlayerView.setKeepScreenOn(isPlaying);
            }
        });
        View viewMask = binding.exoPlayerView.findViewById(R.id.v_gradient_mask);
        binding.exoPlayerView.setControllerVisibilityListener(viewMask::setVisibility);
        binding.setHandler(new Handler());
        retriever = new MediaInfoRetriever(exoPlayer);
        keyHandler = new KeyHandler(exoPlayer);
        osdHelper = new OSDHelper(binding.frOsdSafeZone);
        keyHandler.attachOsd(osdHelper);
        binder = RenderManager.INSTANCE.getRenderService();
        binder.registerController(new ExoRenderControlImpl(exoPlayer, this, state -> {
            if (state != lastTransState) {
                binder.notifyTransportStateChanged(state);
                lastTransState = state;
            }
        }));
        onNewIntent(getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        if (intent == null) return;
        super.onNewIntent(intent);
        String url = intent.getStringExtra(K.EXTRA_KEY_URL);
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
    public void onWindowFocusChanged(boolean hasFocus) {
        LogUtil.d(TAG, "windowFocus->" + hasFocus);
        if (hasFocus) {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
            controllerCompat.hide(WindowInsetsCompat.Type.systemBars());
            controllerCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        exoPlayer.pause();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyHandler.onKeyUp(keyCode, event)) return true;
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyHandler.onKeyDown(keyCode, event)) return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyHandler.onKeyLongPress(keyCode, event)) return true;
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exoPlayer.stop();
        binder.notifyTransportStateChanged(TransportState.STOPPED);
        exoPlayer.release();
        binder.unregisterController();
    }

    @Override
    public void finishPlayer() {
        finish();
    }

    @Override
    public void setMediaEntity(@NonNull MediaEntity mediaEntity) {
        // TODO: 2022/3/25 setTitle
    }

    public final class Handler {
        public void navigationClick(View view) {
            finish();
        }
    }

}