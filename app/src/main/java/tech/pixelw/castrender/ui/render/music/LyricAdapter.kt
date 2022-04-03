package tech.pixelw.castrender.ui.render.music

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.pixelw.castrender.databinding.ItemLyricsBinding
import tech.pixelw.castrender.utils.lrc.LrcParser

class LyricAdapter : RecyclerView.Adapter<LyricAdapter.LyricVH>() {

    var lyrics: List<LrcParser.LrcLine>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LyricVH {
        val binding =
            ItemLyricsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LyricVH(binding)
    }

    override fun onBindViewHolder(holder: LyricVH, position: Int) {
        lyrics?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount() = lyrics?.size ?: 0

    class LyricVH(val binding: ItemLyricsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(lrcLine: LrcParser.LrcLine) {
            binding.lrc = lrcLine
        }
    }
}