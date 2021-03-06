package com.example.nhaccuato.play;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.SeekBar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import com.example.nhaccuato.MainActivity;
import com.example.nhaccuato.R;
import com.example.nhaccuato.Utils.Constants;
import com.example.nhaccuato.Utils.ConvertHelper;
import com.example.nhaccuato.Utils.SaveHelper;
import com.example.nhaccuato.databinding.FragmentPlayBinding;
import com.example.nhaccuato.models.Song;
import com.example.nhaccuato.play.ItemPlayAdapter;
import com.example.nhaccuato.play.MediaManager;
import com.example.nhaccuato.play.PassData;
import com.example.nhaccuato.play.PlayViewModel;
import com.example.nhaccuato.play.notification.OnClearFromRecentService;
import com.example.nhaccuato.play.notification.SongNotificationManager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

public class PlayFragment extends Fragment {
    // Todo: Constant
    private static final String TAG = PlayFragment.class.getSimpleName();
    private static final float PAUSE_LOTTIE_SPEED = 3.0f;
    Handler mHandler;
    // View
    private FragmentPlayBinding fragmentPlayBinding;
    private SlidingUpPanelLayout panelLayout;

    // Todo: Fields
    private MediaManager mMediaManager;
    private List<Song> mSong = new ArrayList<>();
    private PlayViewModel mViewModel;
    private int FLAG_PAGE = -1;
    private ViewPager2PageChangeCallBack pager2PageChangeCallBack;
    private float slideoffset;
    private boolean isExpanding = false;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOnPressedCallBack();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentPlayBinding = FragmentPlayBinding.inflate(inflater, container, false);
        fragmentPlayBinding.setLifecycleOwner(this);

        // pass listener from main activity to this fragment
        ((MainActivity) getActivity()).passVal(new PassData() {
            @Override
            public void onChange(List<Song> songs, int position) {
                // If sliding up panel expanding, we will stop receive called
                if (isExpanding) {
                    return;
                }
                setSong(songs, position, true);
                isExpanding = true;
                panelLayout.setTouchEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExpanding = false;
                        Log.d(TAG, "run: ");
                        panelLayout.setTouchEnabled(true);
                    }
                }, 300);
            }
        });
        // load previous song list and that position
        return fragmentPlayBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMediaManager = new MediaManager(getContext());
        mViewModel = new ViewModelProvider(this).get(PlayViewModel.class);
        mViewModel.setContext(getContext());
        initListener();
        initHandler();
        loadSavedData();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().registerReceiver(mMediaManager.broadcastReceiver, new IntentFilter(Constants.TRACK_CODE));
            Intent clearService = new Intent(getActivity().getBaseContext(), OnClearFromRecentService.class);
            getActivity().startService(clearService);
        }
    }

    private void initOnPressedCallBack() {
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isFullScreen()) {
                    panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
                else {
                    setEnabled(false);
                    getActivity().onBackPressed();
                }
            }
        });
    }

    private boolean isFullScreen() {
        return panelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED;
    }

    private void loadSavedData() {
        List<Song> prevSongs = SaveHelper.loadSong(getActivity());
        int prevPos = SaveHelper.loadCurrentSongPosition(getActivity());
        if (prevSongs.isEmpty()) {
            setSong(new ArrayList<>(), -1, false);
            return;
        }
        setSong(prevSongs, prevPos, false);
    }

    private void initListener() {
        panelLayout = getActivity().findViewById(R.id.sliding_up_panel_main);

        panelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                fragmentPlayBinding.miniContainerController.setAlpha(1f - slideOffset);
                // set alpha of the fullscreen
//                fragmentPlayBinding.vpPlay.setAlpha(slideOffset);
//                fragmentPlayBinding.containerController.setAlpha(slideOffset);
                slideoffset = slideOffset;
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (slideoffset < 0.2 && previousState == SlidingUpPanelLayout.PanelState.DRAGGING) {
                    panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
                else if (slideoffset == 1 && previousState == SlidingUpPanelLayout.PanelState.DRAGGING) {
                    panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
                ((MainActivity) getActivity()).setPanelState(newState);
            }
        });

        fragmentPlayBinding.buttonPlayAndPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaManager.pauseButtonClicked();
            }
        });

        fragmentPlayBinding.btnPlayAndPauseController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaManager.pauseButtonClicked();
            }
        });

        mMediaManager.stateLiveData.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                // if the song isPlaying, then we will display pause
                if (aBoolean) {
                    // play->pause
                    fragmentPlayBinding.buttonPlayAndPause.setSpeed(-PAUSE_LOTTIE_SPEED);
                    fragmentPlayBinding.buttonPlayAndPause.playAnimation();
                    fragmentPlayBinding.btnPlayAndPauseController.setBackgroundResource(R.drawable.ic_baseline_pause_24_orange);
                } else {
                    // pause->play
                    fragmentPlayBinding.buttonPlayAndPause.setSpeed(PAUSE_LOTTIE_SPEED);
                    fragmentPlayBinding.buttonPlayAndPause.playAnimation();
                    fragmentPlayBinding.btnPlayAndPauseController.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24_orange);
                }
            }
        });

        // set next and previous button
        fragmentPlayBinding.buttonBackwardSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaManager.onPrevious();
            }
        });

        fragmentPlayBinding.buttonNextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaManager.onNext();
            }
        });

        fragmentPlayBinding.btnSkipNextController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaManager.onNext();
            }
        });
        // set repeat button
        fragmentPlayBinding.buttonRepeatSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update flag
                mMediaManager.updateStateFlag();
                switch (mMediaManager.getStateFlag()) {
                    // update the status that the app is currently not repeat the song
                    case MediaManager.STATE_NON_REPEAT:
                        fragmentPlayBinding.buttonRepeatSong.setImageResource(R.drawable.ic_icon_feather_repeat_white);
                        break;
                    // update the status that the app is currently repeat the current song
                    case MediaManager.STATE_REPEAT_ONE:
                        fragmentPlayBinding.buttonRepeatSong.setImageResource(R.drawable.ic_baseline_repeat_one_24_orange);
                        break;
                    // update the status that the app is currently shuffle the song
                    case MediaManager.STATE_SHUFFLE:
                        fragmentPlayBinding.buttonRepeatSong.setImageResource(R.drawable.ic_icon_open_random_orange);
                        break;
                    // update the status that the app is currently repeat the song list
                    case MediaManager.STATE_REPEAT:
                        fragmentPlayBinding.buttonRepeatSong.setImageResource(R.drawable.ic_icon_feather_repeat_orange);
                        break;
                }
                // if the repeat do not choose
                // if the repeat has been choosen
            }
        });


        fragmentPlayBinding.sbDurationSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                updateTime(progressValue * 100, mMediaManager.getDuration());
                if (fromUser) {
                    mMediaManager.seekTo(progressValue * 100);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar.animate()
                        .setDuration(500)
                        .setInterpolator(new AccelerateInterpolator())
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                seekBar.setThumb(getResources().getDrawable(R.drawable.seek_thumb_zoom));
                            }
                        }).start();
                seekBar.setTag(R.drawable.seek_thumb_zoom);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.animate()
                        .setDuration(500)
                        .setInterpolator(new AccelerateInterpolator())
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                seekBar.setThumb(getResources().getDrawable(R.drawable.seek_thumb));
                            }
                        }).start();
                seekBar.setTag(R.drawable.seek_thumb);
            }
        });

        mMediaManager.posLiveData.observeForever(new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
//                fragmentPlayBinding.vpPlay.setCurrentItem(integer);
                pager2PageChangeCallBack.onPageSelected(integer);
            }
        });
        // create pager 2 listener
        pager2PageChangeCallBack = new ViewPager2PageChangeCallBack();
        fragmentPlayBinding.vpPlay.registerOnPageChangeCallback(pager2PageChangeCallBack);
        fragmentPlayBinding.vpPlay.setOffscreenPageLimit(20);


    }

    private void playCurrentSong(int position) {
        mMediaManager.play(position);
    }

    private void initHandler() {
        mHandler = new Handler();
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
                    mMediaManager.playNextSong();
                }
                mHandler.postDelayed(this, 1000);
            }
        });
    }

    private void updateSeekBar(long currentPosition, long maxDuration) {
        fragmentPlayBinding.sbDurationSong.setMax((int) maxDuration / 100);
        int mCurrentPosition = (int) currentPosition / 100;
        fragmentPlayBinding.sbDurationSong.setProgress(mCurrentPosition);
    }

    private void updateTime(long currentPosition, long maxDuration) {
        fragmentPlayBinding.textTimeStartSong.setText(ConvertHelper.convertToMinutes(currentPosition));
        if (maxDuration >= 0)
            fragmentPlayBinding.textTimeEndSong.setText(ConvertHelper.convertToMinutes(maxDuration));
    }

    // Todo: inner classes + interfaces

    private void setSong(List<Song> songs, int position, boolean isExpanded) {
        FLAG_PAGE = -1;
        if (isExpanded) {
            // case we click in another song
            panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            // fix the bug that we choose song too fast
        }

        mSong = songs;
        ItemPlayAdapter itemPlayAdapter = new ItemPlayAdapter(mSong);
        fragmentPlayBinding.vpPlay.setAdapter(itemPlayAdapter);
        fragmentPlayBinding.vpPlay.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        mMediaManager.setSongs(songs);
        // this will init the singleton class notification manager
        SongNotificationManager.getInstance().init(getContext(), songs);
        if (position != -1) pager2PageChangeCallBack.onPageSelected(position);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMediaManager.getService().requestAudioFocus(getContext());
        // load current position song without scroll style
        fragmentPlayBinding.vpPlay.setCurrentItem(FLAG_PAGE, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Remove the loop to update times and seekbar
        mHandler.removeCallbacksAndMessages(null);
        // save the list and the position of current song for next time use
        SaveHelper.saveSong(getActivity(), mSong);
        SaveHelper.saveCurrentSongPosition(getActivity(), fragmentPlayBinding.vpPlay.getCurrentItem());

        fragmentPlayBinding.vpPlay.unregisterOnPageChangeCallback(pager2PageChangeCallBack);
        mMediaManager.deleteNotification();
        getActivity().unregisterReceiver(mMediaManager.broadcastReceiver);
        mMediaManager.getService().releasePlayer();
        mMediaManager.getService().removeAudioFocus();
    }

    class ViewPager2PageChangeCallBack extends ViewPager2.OnPageChangeCallback {

        public ViewPager2PageChangeCallBack() {
            super();
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            if (position == FLAG_PAGE) {
                return;
            }
            playCurrentSong(position);
            FLAG_PAGE = position;
            fragmentPlayBinding.vpPlay.setCurrentItem(FLAG_PAGE, false);
            // we will set the tittle and artist name to current song
            fragmentPlayBinding.tvTitleController.setText(mSong.get(position).getNameSong());
            fragmentPlayBinding.tvArtistController.setText(mSong.get(position).getNameArtist());
            updateController(mSong.get(position));
            updateBackground(mSong.get(position));
            updateTime(0, 0);
            updateSeekBar(0, 0);
        }

        private void updateBackground(Song song) {
            fragmentPlayBinding.setSong(song);
        }

        private void updateController(Song song) {
            fragmentPlayBinding.tvTitleController.setText(song.getNameSong());
            fragmentPlayBinding.tvArtistController.setText(song.getNameArtist());
            String url = song.getThumbnail();
            Glide.with(getContext())
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.ic_baseline_music_note_orange)
                    .into(fragmentPlayBinding.imgThumbnailController);
        }

    }
}
