package tech.pixelw.castrender.ui.render.music

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import tech.pixelw.castrender.databinding.ItemLyricsBinding
import tech.pixelw.castrender.ui.render.music.lrc.LyricsListModel
import tech.pixelw.castrender.utils.LogUtil

class LyricAdapter : RecyclerView.Adapter<LyricAdapter.LyricVH>(), Player.Listener,
    Animator.AnimatorListener {

    companion object {
        private const val TAG = "LyricAdapter"
    }

    private var hasPendingFadeOut = false
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
    private val fadeInAnimator = ValueAnimator.ofFloat(0.0f, 1.0f).apply {
        duration = 550L
        addListener(this@LyricAdapter)
    }
    private val fadeOutAnimator = ValueAnimator.ofFloat(1.0f, 0.0f).apply {
        duration = 300L
        addListener(this@LyricAdapter)
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
                return if (curLine.millis() <= millis && millis < nextLine.millis()) {
                    scrollToPos(i - 1)
                    lastIndex = i
                    nextLine.millis() - millis + 100L
                } else if (millis < curLine.millis()) {
                    lastIndex = i
                    curLine.millis() - millis + 100L
                } else {
                    continue
                }
            }
        }

//         if all item iterated, reset cache position
        if (!noCachePos) return scrollTo(millis, true)
        return Long.MAX_VALUE
    }

    private fun scrollToPos(pos: Int) {
        Log.d(TAG, "scrollToPos() called with: pos = $pos")
        val viewAtPos = recyclerView?.run {
            smoothScrollToPosition(pos)
            findViewHolderForAdapterPosition(pos)
        }
        if (viewAtPos is LyricVH) {
            fadeOutAnimator.start()
            hasPendingFadeOut = false
            fadeInAnimator.addUpdateListener(viewAtPos)
            fadeInAnimator.start()
            fadeOutAnimator.addUpdateListener(viewAtPos)
            hasPendingFadeOut = true
        }

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


    class LyricVH(val binding: ItemLyricsBinding) : RecyclerView.ViewHolder(binding.root),
        ValueAnimator.AnimatorUpdateListener {

        fun bind(model: LyricsListModel) {
            binding.model = model
            binding.clLyrics.apply {
                pivotX = 0.0f
                pivotY = (height / 2).toFloat()
            }
        }

        override fun onAnimationUpdate(animation: ValueAnimator) {
            val value = animation.animatedValue as? Float
            value?.let {
                binding.clLyrics.alpha = getAlphaValue(it)
                binding.clLyrics.scaleX = getScaleValue(it)
                binding.clLyrics.scaleY = getScaleValue(it)
            }
        }

        fun getAlphaValue(float: Float): Float {
            return 0.4f + 0.6f * float
        }

        fun getScaleValue(float: Float): Float {
            return 1.0f + 0.1f * float
        }
    }

    override fun onAnimationStart(animation: Animator) {
    }

    override fun onAnimationEnd(animation: Animator) {
        if (animation is ValueAnimator) {
            when (animation) {
                fadeInAnimator -> {
                    animation.removeAllUpdateListeners()
                }
                fadeOutAnimator -> {
                    if (!hasPendingFadeOut) {
                        animation.removeAllUpdateListeners()
                    }
                }
            }
        }

    }

    override fun onAnimationCancel(animation: Animator) {
    }

    override fun onAnimationRepeat(animation: Animator) {
    }
}