package com.example.nhaccuato.list;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;


import com.example.nhaccuato.MainActivity;
import com.example.nhaccuato.R;
import com.example.nhaccuato.databinding.FragmentListBinding;
import com.example.nhaccuato.list.events.ItemEvent;
import com.example.nhaccuato.models.Song;
import com.example.nhaccuato.offline.PlayableItemListener;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {
    private final String TAG = ListFragment.class.getSimpleName();
    private ListViewModel mViewModel;
    private FragmentListBinding fragmentListBinding;
    private List<Song> mSong = new ArrayList<>();
    private RecyclerView recyclerView;
    private SnapHelper snapHelper = new LinearSnapHelper();
    private LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    private ItemEvent buttonEvent;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentListBinding = FragmentListBinding.inflate(inflater, container, false);
        fragmentListBinding.setLifecycleOwner(this);
        return fragmentListBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ListViewModel.class);
        mSong.clear();
        Log.i("TAG", "onActivityCreated");
        mViewModel.loadSong();
        mViewModel.setContext(getContext());
        mViewModel.getSongLiveData().observe(getViewLifecycleOwner(), songs -> {
            if (songs != null) {
                if (!songs.isEmpty()) {
                    mSong.clear();
                    mSong.addAll(songs);
                    setSong();
                }
            }
        });
    }

    private PlayableItemListener playableItemListener = new PlayableItemListener() {
        @Override
        public void onClick(List<Song> songs, int position) {
            ((MainActivity) getActivity()).mPassData.onChange(songs, position);
        }
    };

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    private void setSong() {
        Log.i(TAG, "setSong: " + mSong.size());
        ListSongAdapter listSongAdapter = new ListSongAdapter(mSong, playableItemListener);
        recyclerView = fragmentListBinding.rvList;
        recyclerView.setAdapter(listSongAdapter);
        recyclerView.setLayoutManager(layoutManager);
        snapHelper.attachToRecyclerView(recyclerView);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(0);
//                CardView cardView = viewHolder.itemView.findViewById(R.id.cardview_item_list);
//                cardView.animate().setDuration(200).scaleX(1f).scaleY(1f).setInterpolator(new OvershootInterpolator()).start();
//            }
//        }, 100);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                View view = snapHelper.findSnapView(layoutManager);
                int pos = layoutManager.getPosition(view);

                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(pos);
                CardView cardView = viewHolder.itemView.findViewById(R.id.cardview_item_list);

                if(newState == RecyclerView.SCROLL_STATE_IDLE){

                    cardView.animate().setDuration(200).scaleX(1f).scaleY(1f).setInterpolator(new OvershootInterpolator()).start();

                }else {

                    cardView.animate().setDuration(200).scaleX(0.95f).scaleY(0.95f).setInterpolator(new OvershootInterpolator()).start();

                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });


    }

}