package tech.pixelw.castrender.ui.render.music

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable
import org.fourthline.cling.support.model.TransportState
import tech.pixelw.castrender.K
import tech.pixelw.castrender.R
import tech.pixelw.castrender.databinding.ActivityMusicPlayerBinding
import tech.pixelw.castrender.ui.render.ExoRenderControlImpl
import tech.pixelw.castrender.ui.render.RenderManager
import tech.pixelw.castrender.utils.CenterScrollLLM
import tech.pixelw.castrender.utils.LogUtil
import tech.pixelw.castrender.utils.TopBottomMarginDecoration
import tech.pixelw.cling_common.entity.MediaEntity
import tech.pixelw.dmr_core.DLNARendererService

class MusicPlayerActivity : AppCompatActivity(), ExoRenderControlImpl.ActivityCallback {


    private var service: DLNARendererService? = null
    private lateinit var binding: ActivityMusicPlayerBinding
    private lateinit var vm: MusicViewModel
    private val lyricAdapter = LyricAdapter()
    private lateinit var exoplayer: ExoPlayer
    private lateinit var controllerCompat: WindowInsetsControllerCompat

    /**
     * todo 中间滚动 放大动画 透明度动画
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMusicPlayerBinding?>(
            this,
            R.layout.activity_music_player
        ).apply {
            lifecycleOwner = this@MusicPlayerActivity
            rvLyrics.layoutManager = CenterScrollLLM(this@MusicPlayerActivity)
            rvLyrics.adapter = lyricAdapter
            rvLyrics.addItemDecoration(TopBottomMarginDecoration())
            controllerCompat = ViewCompat.getWindowInsetsController(root)!!
        }
        vm = ViewModelProvider(this).get(MusicViewModel::class.java).apply {
            currentLyrics.observe(this@MusicPlayerActivity) {
                lyricAdapter.lyricsModels = it
            }
            binding.vm = this
        }
        exoplayer = SimpleExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder().setUsage(C.USAGE_MEDIA)
                    .setContentType(C.CONTENT_TYPE_MUSIC).build(), true
            ).build().apply {
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        if (isPlaying) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        } else {
                            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        }
                    }
                })
            }
        lyricAdapter.player = exoplayer
        service = RenderManager.renderService
        service?.registerController(ExoRenderControlImpl(exoplayer, this))
        onNewIntent(intent)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        LogUtil.d(TAG, "windowFocus->$hasFocus")
        if (hasFocus) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            controllerCompat.hide(WindowInsetsCompat.Type.systemBars())
            controllerCompat.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.getStringExtra(K.EXTRA_KEY_URL)?.run {
            if (!TextUtils.isEmpty(this)) {
                prepareAndPlayMedia(this)
            }
        }
        intent?.getParcelableExtra<MediaEntity>(K.EXTRA_KEY_META)?.run {
            setMediaEntity(this)
        }

    }

    private fun prepareAndPlayMedia(url: String) {
        runOnUiThread {
            exoplayer.setMediaItem(MediaItem.fromUri(url))
            exoplayer.prepare()
            exoplayer.play()
        }
    }

    companion object {
        private const val TAG = "MusicPlayerActivity"

        @JvmStatic
        fun newPlayerInstance(context: Context?, uri: String?, entity: MediaEntity) {
            context?.startActivity(Intent(context, MusicPlayerActivity::class.java).apply {
                putExtra(K.EXTRA_KEY_URL, uri)
                putExtra(K.EXTRA_KEY_META, entity)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    override fun finishPlayer() {
        // 网易云切歌会调用stop
//        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoplayer.stop()
        notifyTransportStateChanged(TransportState.STOPPED)
        exoplayer.release()
        service?.unregisterController()
    }

    private fun notifyTransportStateChanged(state: TransportState) {
        service?.avTransportLastChange()?.setEventedValue(
            service?.instanceId,
            AVTransportVariable.TransportState(state)
        )
    }

    override fun setMediaEntity(mediaEntity: MediaEntity) {
        vm.mediaEntity.value = mediaEntity
        mediaEntity.id?.let { vm.fetchLyrics(it) }
    }
}