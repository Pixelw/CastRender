package tech.pixelw.castrender;

import android.app.Application;
import android.content.Context;

/**
 * @author Carl Su "Pixelw"
 * @date 2021/10/25
 */
public class CastRenderApp extends Application {
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
