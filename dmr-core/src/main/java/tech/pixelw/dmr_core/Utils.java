package tech.pixelw.dmr_core;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.text.format.Formatter;

/**
 * @author Carl Su "Pixelw"
 * @date 2021/10/25
 */
public class Utils {

    public static String getWifiIpAddress(Context context){
        WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        return Formatter.formatIpAddress(ip);
    }

    /**
     * 把 00:00:00 格式转成时间戳
     *
     * @param formatTime 00:00:00 时间格式
     * @return 时间戳(毫秒)
     */
    public static long getIntTime(String formatTime) {
        if (!TextUtils.isEmpty(formatTime)) {
            String[] tmp = formatTime.split(":");

            if (tmp.length < 3) {
                return 0;
            }

            int second = Integer.parseInt(tmp[0]) * 3600 + Integer.parseInt(tmp[1]) * 60 + Integer.parseInt(tmp[2]);

            return second * 1000L;
        }

        return 0;
    }
}
