package tech.pixelw.castrender.utils;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * @author Carl Su "Pixelw"
 * @date 2022/2/8
 */
public class SafeZoneHelper {
    private static final String TAG = "SafeZoneHelper";

    public static void observe(View view, boolean systemBarsInsets, boolean displayCutoutInsets, Callback callback) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int typeMask = 0;
            typeMask |= systemBarsInsets ? WindowInsetsCompat.Type.systemBars() : 0;
            typeMask |= displayCutoutInsets ? WindowInsetsCompat.Type.displayCutout() : 0;
            Insets safeInsets = insets.getInsetsIgnoringVisibility(typeMask);
            callback.applyInsets(safeInsets);
            return insets;
        });
    }

    public interface Callback{
        void applyInsets(Insets insets);
    }

}
