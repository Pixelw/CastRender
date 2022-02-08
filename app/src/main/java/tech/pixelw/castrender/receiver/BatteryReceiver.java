package tech.pixelw.castrender.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;


/**
 * @author Carl Su "Pixelw"
 * @date 2022/1/30
 */
public class BatteryReceiver extends BroadcastReceiver {

    private final Callback callback;
    public IntentFilter intentFilter;

    public BatteryReceiver(BatteryReceiver.Callback callback) {
        this.callback = callback;
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);

    }

    @Override
    public void onReceive(Context context, Intent batteryStatus) {
        if (callback == null) return;
        if (batteryStatus.hasExtra(BatteryManager.EXTRA_LEVEL)) {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level * 100 / (float) scale;
            callback.onBatteryStatusChanged(batteryPct);
        }

        if (batteryStatus.hasExtra(BatteryManager.EXTRA_STATUS)) {
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            callback.onChargeStatusChanged(isCharging);
        }
    }


    public interface Callback {
        default void onChargeStatusChanged(boolean charging) {
        }

        default void onBatteryStatusChanged(float percent) {
        }
    }
}
