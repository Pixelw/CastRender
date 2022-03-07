package tech.pixelw.castrender.ui;

import static tech.pixelw.dmr_core.DLNARendererService.NOTIFICHANNEL_ID;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import tech.pixelw.castrender.R;
import tech.pixelw.castrender.databinding.ActivityMainBinding;
import tech.pixelw.castrender.receiver.NetworkReceiver;
import tech.pixelw.castrender.ui.browser.MediaBrowserActivity;
import tech.pixelw.castrender.ui.controller.ControllerActivity;
import tech.pixelw.castrender.ui.render.PlayerActivity;
import tech.pixelw.dmr_core.DLNARendererService;
import tech.pixelw.dmr_core.service.DefaultRenderControl;

public class MainActivity extends AppCompatActivity
        implements NetworkReceiver.Callback {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;
    private boolean serviceRunning;
    private NetworkReceiver networkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setHandler(new Handler());
        // setup receivers
//        networkReceiver = new NetworkReceiver(this);
//        registerReceiver(networkReceiver, networkReceiver.intentFilter);
        startBackgroundService(true);
    }

    private void startBackgroundService(boolean visible) {
        if (serviceRunning) return;
        Intent intent = new Intent(this, DLNARendererService.class);
        if (visible) {
            String text = getString(R.string.app_name) + " is running background.";
            intent.putExtra(NOTIFICHANNEL_ID, text);
        }
        DefaultRenderControl.idlnaNewSession = PlayerActivity::newPlayerInstance;
        startService(intent);
        serviceRunning = true;
    }

    public void stopBackgroundService() {
        stopService(new Intent(this, DLNARendererService.class));
        serviceRunning = false;
    }

    @Override
    public void onNetworkChanged(NetworkInfo networkInfo) {
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
            startBackgroundService(true);
        } else {
            stopBackgroundService();
        }
    }

    public final class Handler {
        public void openOnClick(View v) {
            MainActivity.this.startActivity(new Intent(
                    MainActivity.this, PlayerActivity.class));
        }

        public void mediaBrowserOpen(View v) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, MediaBrowserActivity.class));
        }

        public void controllerOpen(View v) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, ControllerActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }
    }
}