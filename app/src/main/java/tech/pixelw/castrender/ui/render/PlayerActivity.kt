package tech.pixelw.castrender.ui.render

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.fourthline.cling.support.model.TransportState
import tech.pixelw.castrender.K
import tech.pixelw.castrender.R
import tech.pixelw.castrender.databinding.ActivityPlayer2Binding
import tech.pixelw.castrender.ui.mediainfo.MediaInfoListAdapter
import tech.pixelw.castrender.ui.mediainfo.MediaInfoRetriever
import tech.pixelw.castrender.ui.render.ExoRenderControlImpl.ActivityCallback
import tech.pixelw.castrender.ui.render.ExoRenderControlImpl.TransportStateCallback
import tech.pixelw.castrender.ui.render.RenderManager.renderService
import tech.pixelw.castrender.utils.LogUtil
import tech.pixelw.castrender.utils.SafeZoneHelper
import tech.pixelw.cling_common.entity.MediaEntity
import tech.pixelw.dmr_core.DLNARendererService
import kotlin.math.max

class PlayerActivity : AppCompatActivity(), ActivityCallback {

    private lateinit var binding: ActivityPlayer2Binding
    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var binder: DLNARendererService
    private lateinit var keyHandler: KeyHandler
    private lateinit var osdHelper: OSDHelper
    private var controllerCompat: WindowInsetsControllerCompat? = null
    private var safeZone: ViewGroup? = null
    private var tvTitle: TextView? = null
    private var retriever: MediaInfoRetriever? = null
    private var lastTransState = TransportState.CUSTOM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player2)
        controllerCompat = ViewCompat.getWindowInsetsController(binding.exoPlayerView)
        val toolbar: Toolbar = binding.exoPlayerView.findViewById(R.id.player_toolbar)
        setSupportActionBar(toolbar)
        safeZone = binding.exoPlayerView.findViewById(R.id.cl_controller_safe_zone)
        if (safeZone != null) {
            SafeZoneHelper.observe(safeZone, true, true) { insets: Insets ->
                LogUtil.i(TAG, "SafeZone in pixels $insets")
                val lp = safeZone!!.layoutParams as MarginLayoutParams
                val lpOsd = binding.frOsdSafeZone.layoutParams as MarginLayoutParams
                val horizontal = max(
                    max(insets.left, insets.right),
                    max(lp.leftMargin, lp.rightMargin)
                )
                val top = max(lp.topMargin, insets.top)
                val bottom = max(lp.bottomMargin, insets.bottom)
                lp.setMargins(horizontal, top, horizontal, bottom)
                lpOsd.setMargins(horizontal, top, horizontal, bottom)
            }
        }
        if (Build.MODEL.contains("CM101")) {
            val surface = binding.exoPlayerView.videoSurfaceView
            if (surface is SurfaceView) {
                val type = SurfaceHolder.SURFACE_TYPE_NORMAL
                LogUtil.w(TAG, "CM101s-2 surfaceView tweaks $type")
                surface.holder.setType(type)
                surface.holder.setFormat(PixelFormat.RGBA_8888)
            }
        }
        val renderersFactory =
            DefaultRenderersFactory(this).setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
        exoPlayer = SimpleExoPlayer.Builder(this, renderersFactory)
            .setSeekForwardIncrementMs(5000)
            .setSeekBackIncrementMs(5000)
            .setAudioAttributes(
                AudioAttributes.Builder().setUsage(C.USAGE_MEDIA)
                    .setContentType(C.CONTENT_TYPE_MOVIE).build(), true
            )
            .build()
//        val selector = DefaultTrackSelector(this)
//        selector.setParameters(selector.buildUponParameters().setViewportSize(1600, 900, false))
        binding.exoPlayerView.player = exoPlayer
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                binding.exoPlayerView.keepScreenOn = isPlaying
            }
        })
        val viewMask = binding.exoPlayerView.findViewById<View>(R.id.v_gradient_mask)
        binding.exoPlayerView.setControllerVisibilityListener { visibility: Int ->
            viewMask.visibility = visibility
        }
        tvTitle = binding.exoPlayerView.findViewById(R.id.ctrl_title)
        binding.handler = Handler()
        retriever = MediaInfoRetriever(exoPlayer)
        keyHandler = KeyHandler(exoPlayer)
        osdHelper = OSDHelper(binding.frOsdSafeZone)
        keyHandler.attachOsd(osdHelper)
        binder = renderService
        binder.registerController(
            ExoRenderControlImpl(
                exoPlayer,
                this,
                object : TransportStateCallback {
                    override fun notify(state: TransportState) {
                        if (state != lastTransState) {
                            binder.notifyTransportStateChanged(state)
                            lastTransState = state
                        }
                    }
                }
            )
        )
        onNewIntent(intent)
        // test hisi
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        LogUtil.w(TAG, "w" + displayMetrics.widthPixels + " h" + displayMetrics.heightPixels)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val url = intent.getStringExtra(K.EXTRA_KEY_URL)
        if (!TextUtils.isEmpty(url)) {
            prepareAndPlayMedia(url)
        }
        val meta = intent.getParcelableExtra<MediaEntity?>(K.EXTRA_KEY_META)
        if (meta != null) {
            setMediaEntity(meta)
        }
    }

    private fun prepareAndPlayMedia(url: String?) {
        runOnUiThread {
            val mediaItem = MediaItem.fromUri(
                url!!
            )
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_player, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_media_info) {
            val view = layoutInflater.inflate(R.layout.layout_media_info, null)
            view.findViewById<RecyclerView>(R.id.list_media_info)?.apply {
                layoutManager = LinearLayoutManager(this@PlayerActivity)
                val mediaInfoListAdapter = MediaInfoListAdapter()
                mediaInfoListAdapter.setTrackList(retriever!!.mediaInfo.tracks)
                adapter = mediaInfoListAdapter
                val dialog = MaterialAlertDialogBuilder(this@PlayerActivity)
                    .setTitle("MediaInfo")
                    .setView(view)
                    .create()
                dialog.show()
            }
            // TODO: 2022/2/10 list

        } else if (item.itemId == R.id.menu_speed_settings) {
        }
        return true
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        LogUtil.d(TAG, "windowFocus->$hasFocus")
        if (hasFocus) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            controllerCompat!!.hide(WindowInsetsCompat.Type.systemBars())
            controllerCompat!!.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayer.pause()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyHandler.onKeyUp(keyCode, event)) true else super.onKeyUp(keyCode, event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyHandler.onKeyDown(keyCode, event)) true else super.onKeyDown(keyCode, event)
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyHandler.onKeyLongPress(keyCode, event)) true else super.onKeyLongPress(
            keyCode,
            event
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.stop()
        binder.notifyTransportStateChanged(TransportState.STOPPED)
        exoPlayer.release()
        binder.unregisterController()
    }

    override fun finishPlayer() {
        finish()
    }

    override fun setMediaEntity(mediaEntity: MediaEntity) {
        tvTitle!!.text = mediaEntity.title
    }

    inner class Handler {
        fun navigationClick(view: View?) {
            finish()
        }
    }

    companion object {
        private const val TAG = "PlayerActivity"
        fun newPlayerInstance(context: Context?, url: String?, entity: MediaEntity?) {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(K.EXTRA_KEY_URL, url)
            intent.putExtra(K.EXTRA_KEY_META, entity)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(intent)
        }
    }
}