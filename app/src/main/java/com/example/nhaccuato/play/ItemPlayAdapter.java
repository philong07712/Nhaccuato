package com.example.nhaccuato.play;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhaccuato.Models.Song;
import com.example.nhaccuato.R;
import com.example.nhaccuato.databinding.ItemPlayBinding;

import java.util.ArrayList;
import java.util.List;

public class ItemPlayAdapter extends RecyclerView.Adapter<ItemPlayAdapter.ViewHolder> {

    private List<Song> mSongList = new ArrayList<>();

    public void setmSongList(List<Song> mSongList) {
        this.mSongList = mSongList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemPlayBinding itemBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.item_play, parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = mSongList.get(position);
        holder.bind(song);
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemPlayBinding itemLayoutPlayBinding;

        public ViewHolder(@NonNull ItemPlayBinding itemLayoutPlayBinding) {
            super(itemLayoutPlayBinding.getRoot());
            this.itemLayoutPlayBinding = itemLayoutPlayBinding;
        }

        public void bind(Song item) {
            itemLayoutPlayBinding.setSong(item);
            itemLayoutPlayBinding.executePendingBindings();
        }
    }
}
