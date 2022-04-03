package tech.pixelw.castrender.ui.render.music

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import tech.pixelw.castrender.R
import tech.pixelw.castrender.databinding.ActivityMusicPlayerBinding

class MusicPlayerActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMusicPlayerBinding
    private lateinit var vm: MusicViewModel
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
            rvLyrics.layoutManager = LinearLayoutManager(this@MusicPlayerActivity)
            rvLyrics.adapter = lyricAdapter
        }
        vm = ViewModelProvider(this).get(MusicViewModel::class.java).apply {
            currentLyrics.observe(this@MusicPlayerActivity) {
                lyricAdapter.lyrics = it
            }
            fetchLyrics("1803908863")
        }
    }

    companion object {
        private const val TAG = "MusicPlayerActivity"
    }
}