package tech.pixelw.castrender.ui.browser

import androidx.recyclerview.widget.RecyclerView
import tech.pixelw.castrender.databinding.ItemMediaBrowserBinding
import tech.pixelw.castrender.ui.browser.entity.BrowserItem

class MediaViewHolder(val binding: ItemMediaBrowserBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: BrowserItem) {
        binding.item = item
    }
}