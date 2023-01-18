package tech.pixelw.castrender.feature.render.music

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import com.google.android.exoplayer2.C
import tech.pixelw.castrender.K
import tech.pixelw.castrender.R
import tech.pixelw.castrender.databinding.ActivityMusicPlayerBinding
import tech.pixelw.castrender.feature.render.RenderControlImpl
import tech.pixelw.castrender.feature.render.player.ExoPlayerImplementation
import tech.pixelw.castrender.feature.render.player.IPlayer
import tech.pixelw.castrender.utils.CenterScrollLLM
import tech.pixelw.castrender.utils.LogUtil
import tech.pixelw.castrender.utils.TopBottomMarginDecoration
import tech.pixelw.cling_common.entity.MediaEntity

class MusicPlayerActivity : AppCompatActivity(), RenderControlImpl.ActivityCallback {

    private lateinit var binding: ActivityMusicPlayerBinding
    private val vm by viewModels<MusicViewModel>()
    private lateinit var controllerCompat: WindowInsetsControllerCompat
    private lateinit var player: IPlayer<*>
    private val lyricAdapter = LyricAdapter()

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
        vm.apply {
            currentLyrics.observe(this@MusicPlayerActivity) {
                lyricAdapter.lyricsModels = it
            }
            binding.vm = this
        }
        player = ExoPlayerImplementation(this, audioType = C.AUDIO_CONTENT_TYPE_MUSIC)
        lyricAdapter.player = player

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
            player.prepareMedia(url)
        }
    }

    companion object {
        private const val TAG = "MusicPlayerActivity"

        @JvmStatic
        fun newPlayerInstance(context: Context?, uri: String?, entity: MediaEntity?) {
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
        player.close()
        vm.disconnect()
    }

    override fun setMediaEntity(mediaEntity: MediaEntity) {
        vm.media.value = mediaEntity
        mediaEntity.id?.let { vm.fetchLyrics(it) }
    }


}