package tech.pixelw.castrender.ui.render

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tech.pixelw.castrender.K
import tech.pixelw.castrender.R
import tech.pixelw.castrender.databinding.ActivityPlayerBinding
import tech.pixelw.castrender.ui.mediainfo.MediaInfoListAdapter
import tech.pixelw.castrender.ui.mediainfo.MediaInfoRetriever
import tech.pixelw.castrender.ui.render.player.ExoPlayerImplementation
import tech.pixelw.castrender.ui.render.player.IPlayer
import tech.pixelw.castrender.utils.LogUtil
import tech.pixelw.cling_common.entity.MediaEntity

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var player: IPlayer<*>
    private lateinit var keyHandler: KeyHandler
    private lateinit var osdHelper: OSDHelper
    private lateinit var controllerCompat: WindowInsetsControllerCompat
    private val viewModel by viewModels<PlayerViewModel>()
    private var retriever: MediaInfoRetriever? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player)
        binding.lifecycleOwner = this
        controllerCompat = WindowCompat.getInsetsController(window, binding.root)

        // TODO: SLY 2023/1/17 switch
        val preferencePlayer = viewModel.getPreferencePlayer()
        when (preferencePlayer) {

        }
        player.bindView(binding.frPlayContainer)
        if (player is ExoPlayerImplementation) {
            retriever = MediaInfoRetriever(player.playerInstance() as ExoPlayer)
        }
        keyHandler = KeyHandler(player)
        osdHelper = OSDHelper(binding.clSafezone)
        keyHandler.attachOsd(osdHelper)

        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val url = intent.getStringExtra(K.EXTRA_KEY_URL)
        if (!TextUtils.isEmpty(url)) {
            prepareAndPlayMedia(url)
        }
        val meta: MediaEntity? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(K.EXTRA_KEY_META, MediaEntity::class.java)
        } else {
            intent.getParcelableExtra(K.EXTRA_KEY_META)
        }
        if (meta != null) {
            viewModel.media.value = meta
        }
    }

    private fun prepareAndPlayMedia(url: String?) {
        runOnUiThread {
            if (url != null) {
                player.prepareMedia(url, playWhenReady = true)
            }
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

        }
        return true
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        LogUtil.d(TAG, "windowFocus->$hasFocus")
        if (hasFocus) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            controllerCompat.hide(WindowInsetsCompat.Type.systemBars())
            controllerCompat.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyHandler.onKeyUp(keyCode, event)) true else super.onKeyUp(keyCode, event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyHandler.onKeyDown(keyCode, event)) true else super.onKeyDown(keyCode, event)
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyHandler.onKeyLongPress(keyCode, event)) true
        else super.onKeyLongPress(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        player.close()
        viewModel.disconnect()
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