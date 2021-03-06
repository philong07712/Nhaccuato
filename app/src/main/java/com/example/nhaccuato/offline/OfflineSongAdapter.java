package com.example.nhaccuato.offline;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.nhaccuato.R;
import com.example.nhaccuato.databinding.ItemOfflineBinding;
import com.example.nhaccuato.models.Song;
import com.example.nhaccuato.offline.PlayableItemListener;

import java.util.ArrayList;
import java.util.List;

public class OfflineSongAdapter extends RecyclerView.Adapter<OfflineSongAdapter.OfflineViewHolder> {

    private List<Song> mSongList = new ArrayList<>();
    public PlayableItemListener itemListener;
    public OfflineSongAdapter(List<Song> mSongList, PlayableItemListener itemListener) {
        this.mSongList = mSongList;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public OfflineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemOfflineBinding itemLayoutOfflineBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_offline, parent, false);
        return new OfflineViewHolder(itemLayoutOfflineBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OfflineViewHolder holder, int position) {
        Song song = mSongList.get(position);
        holder.bind(song);
        holder.itemLayoutOfflineBinding.containerItemOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(mSongList, position);
            }
        });
    }

    private void onItemClick(List<Song> songs, int position) {
        itemListener.onClick(songs, position);
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

    class OfflineViewHolder extends RecyclerView.ViewHolder {
        private final ItemOfflineBinding itemLayoutOfflineBinding;
        public OfflineViewHolder(@NonNull ItemOfflineBinding itemLayoutOfflineBinding) {
            super(itemLayoutOfflineBinding.getRoot());
            this.itemLayoutOfflineBinding = itemLayoutOfflineBinding;
        }

        public void bind(Song item) {
            itemLayoutOfflineBinding.setSong(item);
            itemLayoutOfflineBinding.executePendingBindings();
            Glide.with(itemLayoutOfflineBinding.getRoot())
                    .load(item.getThumbnail())
                    .placeholder(R.drawable.ic_baseline_music_note_orange)
                    .into(itemLayoutOfflineBinding.imgThumbnailItemOffline);
        }
    }
}
