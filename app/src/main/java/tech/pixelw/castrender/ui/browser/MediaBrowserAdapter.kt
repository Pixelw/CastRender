package tech.pixelw.castrender.ui.browser

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import tech.pixelw.castrender.databinding.ItemMediaBrowserBinding
import tech.pixelw.castrender.ui.browser.entity.BrowserItem

class MediaBrowserAdapter(val context: Context) :
    ListAdapter<BrowserItem, MediaViewHolder>(diffUtil) {

    var itemClickHandler: MediaBrowserActivity.ItemHandler? = null

    override fun getItemViewType(position: Int) = getItem(position).type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ItemMediaBrowserBinding.inflate(LayoutInflater.from(context))
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        itemClickHandler?.let { holder.binding.clickListener = it }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<BrowserItem>() {
            override fun areItemsTheSame(oldItem: BrowserItem, newItem: BrowserItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: BrowserItem, newItem: BrowserItem): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }
}