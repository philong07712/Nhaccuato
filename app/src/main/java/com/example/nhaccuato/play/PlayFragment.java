package com.example.nhaccuato.play;

import androidx.lifecycle.ViewModelProvider;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.Observer;

import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.nhaccuato.Models.Song;
import com.example.nhaccuato.Models.SongResponse;
import com.example.nhaccuato.R;
import com.example.nhaccuato.Utils.ConvertHelper;
import com.example.nhaccuato.databinding.FragmentPlayBinding;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlayFragment extends Fragment {
    // Todo: Constant
    private static final String TAG = PlayFragment.class.getSimpleName();

    // View

    private FragmentPlayBinding fragmentPlayBinding;
    // Todo: Fields
    private MediaManager mMediaManager;
    private List<Song> mSong = new ArrayList<>();
    private PlayViewModel mViewModel;

    private Subscriber<List<SongResponse>> response = new Subscriber<List<SongResponse>>() {
        @Override
        public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(List<SongResponse> songResponses) {
            for(SongResponse songResponse : songResponses){
                Song song = new Song();
                mSong.add(song);
            }
            // create manager
            mMediaManager.setSongs(mSong);
            setSong();
            // this will init the singleton class notification manager
        }

        @Override
        public void onError(Throwable t) {
            Log.e("SongResponse Fragment", t.getMessage());
        }

        @Override
        public void onComplete() {
            Log.e("onComplete", "Complete");
        }
    };

    private void setSong() {
        Log.d("TAG", Integer.toString(mSong.size()));
        ItemPlayAdapter itemPlayAdapter = new ItemPlayAdapter();
        itemPlayAdapter.setmSongList(mSong);
        fragmentPlayBinding.vpPlay.setAdapter(itemPlayAdapter);
        fragmentPlayBinding.vpPlay.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentPlayBinding = FragmentPlayBinding.inflate(inflater, container, false);
        fragmentPlayBinding.setLifecycleOwner(this);
        return fragmentPlayBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMediaManager = new MediaManager(getContext());
        mViewModel = new ViewModelProvider(this).get(PlayViewModel.class);
        mViewModel.setContext(getContext());
        mViewModel.getmSongResponeFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response);
        initListener();
        initHandler();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().registerReceiver(mMediaManager.broadcastReceiver, new IntentFilter(Constants.TRACK_CODE));
            Intent clearService = new Intent(getActivity().getBaseContext(), OnClearFromRecentService.class);
            getActivity().startService(clearService);
        }
    }

    private void initListener() {
        fragmentPlayBinding.buttonPlayAndPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaManager.pauseButtonClicked();
            }
        });

        fragmentPlayBinding.sbDurationSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMediaManager.seekTo(progress * 100);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mMediaManager.posLiveData.observeForever(new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                fragmentPlayBinding.vpPlay.setCurrentItem(integer);
            }
        });

        fragmentPlayBinding.vpPlay.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                playCurrentSong(position);
                updateTime(0, 0);
                updateSeekBar(0, 0);
            }
        });
    }

    private void playCurrentSong(int position) {
        mMediaManager.play(position);
    }

    private void initHandler() {
        Handler mHandler = new Handler();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMediaManager.isPlaying()) {
                    long currentPosition = mMediaManager.getCurrentPosition();
                    long maxDuration = mMediaManager.getDuration();
                    updateSeekBar(currentPosition, maxDuration);
                    updateTime(currentPosition, maxDuration);
                }
                // if the song is loaded
                // and the current position is lower than duration with 1s
                if (mMediaManager.isSongCompleted()) {
                    playNextSong();
                }
                mHandler.postDelayed(this, 1000);
            }
        });
    }


    private void playNextSong() {
        mMediaManager.onNext();
    }

    private void updateSeekBar(long currentPosition, long maxDuration) {
        fragmentPlayBinding.sbDurationSong.setMax((int)maxDuration / 100);
        int mCurrentPosition = (int) currentPosition / 100;
        fragmentPlayBinding.sbDurationSong.setProgress(mCurrentPosition);
    }

    private void updateTime(long currentPosition, long maxDuration) {
        fragmentPlayBinding.textTimeStartSong.setText(ConvertHelper.convertToMinutes(currentPosition));
        if (maxDuration >= 0) fragmentPlayBinding.textTimeEndSong.setText(ConvertHelper.convertToMinutes(maxDuration));

    }

    // Todo: inner classes + interfaces

    @Override
    public void onStart() {
        super.onStart();
        mMediaManager.getService().requestAudioFocus(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

