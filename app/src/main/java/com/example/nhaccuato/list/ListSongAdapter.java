
package com.example.nhaccuato.list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhaccuato.R;
import com.example.nhaccuato.databinding.ItemLayoutListBinding;
import com.example.nhaccuato.list.events.ItemEvent;
import com.example.nhaccuato.models.Song;

import java.util.ArrayList;
import java.util.List;

public class ListSongAdapter extends RecyclerView.Adapter<ListSongAdapter.ViewHolder> {

    private List<Song> mSongList = new ArrayList<>();

    public void setmSongList(List<Song> mSongList) {
        this.mSongList = mSongList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemLayoutListBinding itemLayoutListBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_layout_list, parent, false);
        return new ViewHolder(itemLayoutListBinding);
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
        private final ItemLayoutListBinding itemLayoutListBinding;
        public ViewHolder(@NonNull ItemLayoutListBinding itemLayoutListBinding) {
            super(itemLayoutListBinding.getRoot());
            this.itemLayoutListBinding = itemLayoutListBinding;
        }
        public void bind(Song item){
            itemLayoutListBinding.setSong(item);
            itemLayoutListBinding.executePendingBindings();
            ItemEvent itemEvent = new ItemEvent();
            itemLayoutListBinding.setButtonEvents(itemEvent);
        }
    }
}

