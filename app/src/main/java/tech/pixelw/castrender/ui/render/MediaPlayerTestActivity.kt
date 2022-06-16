package tech.pixelw.castrender.ui.render

import android.graphics.Point
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import tech.pixelw.castrender.R

class MediaPlayerTestActivity : AppCompatActivity(), MediaPlayer.OnPreparedListener,
    SurfaceHolder.Callback {

    companion object {
        const val URL = "https://lapi.pixelw.tech/suna.mp4"
    }

    private lateinit var surfaceView: SurfaceView
    private lateinit var mp: MediaPlayer

    //    private val handler: Handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_player_test)
        surfaceView = findViewById(R.id.mp_surface)

        val windowInsetsControllerCompat = WindowInsetsControllerCompat(window, surfaceView)
        windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsControllerCompat.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        val point = Point()
        windowManager.defaultDisplay.getRealSize(point)

        val holder = surfaceView.holder
//        holder.setFixedSize(point.x, point.y)
        holder.addCallback(this)
        mp = MediaPlayer()
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mp.setDisplay(holder)
        mp.setDataSource(this, Uri.parse(URL))
        mp.setOnPreparedListener(this)
        mp.prepareAsync()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }
}