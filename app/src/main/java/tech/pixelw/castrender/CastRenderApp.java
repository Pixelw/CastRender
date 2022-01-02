package tech.pixelw.castrender;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

/**
 * @author Carl Su "Pixelw"
 * @date 2021/10/25
 */
public class CastRenderApp extends MultiDexApplication {
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }
}
