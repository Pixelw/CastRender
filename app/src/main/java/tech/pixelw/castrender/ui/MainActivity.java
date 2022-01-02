package tech.pixelw.castrender.ui;

import static tech.pixelw.dmr_core.DLNARendererService.NOTIFICHANNEL_ID;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import tech.pixelw.castrender.R;
import tech.pixelw.castrender.databinding.ActivityMainBinding;
import tech.pixelw.dmr_core.DLNARendererService;
import tech.pixelw.dmr_core.service.AVTransportController;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setHandler(new Handler());
        startBackgroundAService(true);
    }

    private void startBackgroundAService(boolean visible) {
        Intent intent = new Intent(this, DLNARendererService.class);
        if (visible) {
            String text = getString(R.string.app_name) + "is running background.";
            intent.putExtra(NOTIFICHANNEL_ID, text);
        }
        AVTransportController.idlnaNewSession = PlayerActivity::newPlayerInstance;
        startService(intent);
    }

    public final class Handler {
        public void openOnClick(View v) {
            MainActivity.this.startActivity(new Intent(
                    MainActivity.this, PlayerActivity.class));
        }
    }

}