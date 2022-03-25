package tech.pixelw.castrender.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import tech.pixelw.castrender.R;
import tech.pixelw.castrender.ui.browser.MediaBrowserActivity;
import tech.pixelw.castrender.ui.controller.ControllerActivity;
import tech.pixelw.castrender.ui.render.MusicPlayerActivity;
import tech.pixelw.castrender.ui.render.PlayerActivity;
import tech.pixelw.castrender.ui.render.RenderManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tech.pixelw.castrender.databinding.ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setHandler(new Handler());
        RenderManager.INSTANCE.getRenderService().hello();
    }

    public final class Handler {
        public void openOnClick(View v) {
            MainActivity.this.startActivity(new Intent(
                    MainActivity.this, MusicPlayerActivity.class));
        }

        public void mediaBrowserOpen(View v) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, MediaBrowserActivity.class));
        }

        public void controllerOpen(View v) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, ControllerActivity.class));
        }
    }
}