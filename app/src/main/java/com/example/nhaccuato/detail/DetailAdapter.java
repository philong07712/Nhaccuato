package com.example.nhaccuato.detail;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhaccuato.R;
import com.example.nhaccuato.databinding.ItemLayoutDetailBinding;
import com.example.nhaccuato.detail.events.ItemEvent;
import com.example.nhaccuato.models.Song;

import java.util.ArrayList;
import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {
    private List<Song> songArrayList = new ArrayList<>();

    public void setSongArrayList(List<Song> songArrayList) {
        this.songArrayList = songArrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemLayoutDetailBinding layoutDetailBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_layout_detail, parent, false    );
        return new ViewHolder(layoutDetailBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songArrayList.get(position);
        holder.bind(song);
    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemLayoutDetailBinding itemLayoutDetailBinding;
        public ViewHolder(@NonNull ItemLayoutDetailBinding itemLayoutDetailBinding) {
            super(itemLayoutDetailBinding.getRoot());
            this.itemLayoutDetailBinding = itemLayoutDetailBinding;
        }
        public void bind(Song item){
            itemLayoutDetailBinding.setSong(item);
            itemLayoutDetailBinding.executePendingBindings();
            ItemEvent itemEvent = new ItemEvent();
            itemLayoutDetailBinding.setItemEvent(itemEvent);
        }
    }
}
