package tech.pixelw.castrender.feature.mediainfo;

import androidx.recyclerview.widget.RecyclerView;

import tech.pixelw.castrender.databinding.ItemMediaInfoBinding;

public class MediaInfoViewHolder extends RecyclerView.ViewHolder {
    private tech.pixelw.castrender.databinding.ItemMediaInfoBinding binding;

    public MediaInfoViewHolder(ItemMediaInfoBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
    public void bind(MediaInfo.Track track){
        binding.setTrack(track);
    }
}
