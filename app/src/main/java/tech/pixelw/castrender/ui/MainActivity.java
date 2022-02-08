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
import tech.pixelw.castrender.receiver.BatteryReceiver;
import tech.pixelw.castrender.receiver.NetworkReceiver;
import tech.pixelw.dmr_core.DLNARendererService;
import tech.pixelw.dmr_core.service.DefaultRenderControl;

public class MainActivity extends AppCompatActivity
        implements BatteryReceiver.Callback, NetworkReceiver.Callback {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;
    private boolean serviceRunning;
    private NetworkReceiver networkReceiver;
    private BatteryReceiver batteryReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setHandler(new Handler());
        // setup receivers
        networkReceiver = new NetworkReceiver(this);
        registerReceiver(networkReceiver, networkReceiver.intentFilter);
        batteryReceiver = new BatteryReceiver(this);
        registerReceiver(batteryReceiver, batteryReceiver.intentFilter);
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
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            startBackgroundService(true);
        } else {
            stopBackgroundService();
        }
    }

    @Override
    public void onChargeStatusChanged(boolean charging) {

    }

    @Override
    public void onBatteryStatusChanged(float percent) {

    }

    public final class Handler {
        public void openOnClick(View v) {
            MainActivity.this.startActivity(new Intent(
                    MainActivity.this, PlayerActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }
        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver);
        }
    }
}