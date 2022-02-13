package tech.pixelw.castrender.ui.mediainfo;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tech.pixelw.castrender.R;
import tech.pixelw.castrender.databinding.ItemMediaInfoBinding;

public class MediaInfoListAdapter extends RecyclerView.Adapter<MediaInfoViewHolder> {
    private List<MediaInfo.Track> trackList;

    @NonNull
    @Override
    public MediaInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMediaInfoBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_media_info, parent, false);
        return new MediaInfoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaInfoViewHolder holder, int position) {
        if (trackList == null) {
            return;
        }
        MediaInfo.Track track = trackList.get(position);
        holder.bind(track);
    }

    @Override
    public int getItemCount() {
        if (trackList == null) {
            return 0;
        }
        return trackList.size();
    }

    public void setTrackList(List<MediaInfo.Track> trackList) {
        this.trackList = trackList;
        notifyDataSetChanged();
    }
}
