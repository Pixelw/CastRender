package tech.pixelw.castrender.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author Carl Su "Pixelw"
 * @date 2022/1/30
 */
public class NetworkReceiver extends BroadcastReceiver {
    private Callback callback;
    public IntentFilter intentFilter;

    public NetworkReceiver(Callback callback) {
        this.callback = callback;
        intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) return;
        callback.onNetworkChanged(networkInfo);
    }

    public interface Callback{
        void onNetworkChanged(NetworkInfo networkInfo);
    }
}
