package tech.pixelw.castrender.feature.browser

import androidx.recyclerview.widget.RecyclerView
import tech.pixelw.castrender.databinding.ItemMediaBrowserBinding
import tech.pixelw.castrender.feature.browser.entity.BrowserItem

class MediaViewHolder(val binding: ItemMediaBrowserBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: BrowserItem) {
        binding.item = item
    }
}