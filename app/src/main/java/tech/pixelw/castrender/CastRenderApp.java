package tech.pixelw.castrender;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

import tech.pixelw.castrender.utils.report.BuglyReporter;
import tech.pixelw.castrender.utils.report.Reporter;

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
        Reporter.INSTANCE.setSolution(new BuglyReporter());
    }

    public static Context getAppContext() {
        return appContext;
    }
}
