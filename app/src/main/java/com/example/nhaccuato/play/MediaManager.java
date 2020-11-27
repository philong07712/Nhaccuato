package com.example.nhaccuato.play;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;

import com.example.nhaccuato.ExoPlayerService;
import com.example.nhaccuato.Models.Song;

import java.util.List;

public class MediaManager implements Playable {
    Context mContext;
    List<Song> mSongs;
    private ExoPlayerService mService;
    MutableLiveData<Integer> posLiveData = new MutableLiveData<>();
    int position = 0;

    public MediaManager(Context context) {
        mContext = context;
        mService = new ExoPlayerService(context);
    }

    public void pauseButtonClicked() {
        if (mService.isPlaying()) {
            pauseMedia();
        }
        else resumeMedia();
    }

    public void play(int position) {
        this.position = position;
        mService.playMedia(position);
    }

    public void pauseMedia() {
        mService.pauseMedia();
    }

    public void resumeMedia() {
        mService.resumeMedia();
    }

    public void seekTo(int progress) {
        mService.getPlayer().seekTo(progress);
    }
    public boolean isSongCompleted() {
        return getDuration() != 0
                && getCurrentPosition() > getDuration() - 1000;
    }

    public void setSongs(List<Song> mSongs) {
        this.mSongs = mSongs;
    }

    public ExoPlayerService getService() {
        return mService;
    }

    public long getDuration() {
        return mService.getPlayer().getDuration();
    }

    public long getCurrentPosition() {
        return mService.getPlayer().getCurrentPosition();
    }

    public boolean isPlaying() {
        return true;
    }

    @Override
    public void onPrevious() {
        position--;
        posLiveData.setValue(position);
    }

    @Override
    public void onPlay() {
        mService.resumeMedia();
    }

    @Override
    public void onPause() {
        mService.pauseMedia();
    }

    @Override
    public void onNext() {
        position++;
        posLiveData.setValue(position);
    }
}
