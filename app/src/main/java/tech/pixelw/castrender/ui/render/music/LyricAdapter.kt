package tech.pixelw.castrender.ui.render.music

import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import tech.pixelw.castrender.databinding.ItemLyricsBinding
import tech.pixelw.castrender.ui.render.music.lrc.LyricsListModel
import tech.pixelw.castrender.utils.LogUtil

class LyricAdapter : RecyclerView.Adapter<LyricAdapter.LyricVH>(), Player.Listener {

    companion object {
        private const val TAG = "LyricAdapter"
    }

    var recyclerView: RecyclerView? = null
    var lyricsModels: List<LyricsListModel>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var player: ExoPlayer? = null
        set(value) {
            field = value
            field?.addListener(this)
        }

    private val handler = android.os.Handler(Looper.getMainLooper())
    private val refreshJob = object : Runnable {
        override fun run() {
            player?.currentPosition?.let {
                val untilNext = scrollTo(it)
                LogUtil.d(TAG, "next lrc until $untilNext")
                if (untilNext < Long.MAX_VALUE && untilNext >= 0) {
                    handler.postDelayed(this, untilNext)
                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LyricVH {
        val binding =
            ItemLyricsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LyricVH(binding)
    }

    override fun onBindViewHolder(holder: LyricVH, position: Int) {
        lyricsModels?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount() = lyricsModels?.size ?: 0

    private fun startSync() {
        stopSync()
        LogUtil.d(TAG, "startSync() called")
        handler.postDelayed(refreshJob, 300L)
    }

    private fun stopSync() {
        handler.removeCallbacks(refreshJob)
        lastIndex = 1
    }

    /**
     * cached position
     */
    private var lastIndex = 1

    private fun scrollTo(millis: Long, noCachePos: Boolean = false): Long {
        if (itemCount < 2) return Long.MAX_VALUE
        if (noCachePos) {
            lastIndex = 1
            LogUtil.w(TAG, "noCachePos, Davey!!!")
        }
        lyricsModels?.let {
            for (i in lastIndex until it.size) {
                val curLine = it[i - 1]
                val nextLine = it[i]
                if (curLine.millis() <= millis && millis < nextLine.millis()) {
                    scrollToPos(i - 1)
                    return nextLine.millis() - millis + 100L
                } else if (millis < curLine.millis()) {
                    scrollToPos(i - 1)
                    return curLine.millis() - millis + 100L
                } else {
                    scrollToPos(i)
                }
                lastIndex = i
            }
        }

//         if all item iterated, reset cache position
        if (!noCachePos) return scrollTo(millis, true)
        return Long.MAX_VALUE
    }

    private fun scrollToPos(pos: Int) {
        recyclerView?.smoothScrollToPosition(pos)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (isPlaying) {
            startSync()
        } else {
            stopSync()
        }
    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        if (reason == Player.DISCONTINUITY_REASON_SEEK) {
            startSync()
        }
    }


    class LyricVH(val binding: ItemLyricsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: LyricsListModel) {
            binding.model = model
        }
    }
}