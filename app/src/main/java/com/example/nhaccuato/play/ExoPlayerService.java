package com.example.nhaccuato.play;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.nhaccuato.play.notification.SongNotificationManager;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class ExoPlayerService implements AudioManager.OnAudioFocusChangeListener,
        Player.EventListener {
    private static final String TAG = ExoPlayerService.class.getSimpleName();
    private final int DEFAULT_MIN_BUFFER_MS = 30000;
    private final int DEFAULT_MAX_BUFFER_MS = 60000;
    private final int DEFAULT_BUFFER_FOR_PLAYBACK_MS = 1000;
    private final int DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 5000;

    private AudioManager mAudioManager;
    private Context mContext;
    private SimpleExoPlayer mPlayer = null;
    private Uri mMediaFile;
    private int mPosition = 0;
    private long resumePoint;
    MutableLiveData<Boolean> stateLiveData = new MutableLiveData<>();

    public ExoPlayerService(Context context) {
        mContext = context;
        initPlayer();
    }

    public void initPlayer() {
        // if the player is not exist then we will create one
        if (mPlayer == null) {
            DefaultLoadControl.Builder builder = new DefaultLoadControl.Builder().setBufferDurationsMs(
                    DEFAULT_MIN_BUFFER_MS,
                    DEFAULT_MAX_BUFFER_MS,
                    DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                    DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
            );
            DefaultLoadControl defaultLoadControl = builder.createDefaultLoadControl();
            mPlayer = ExoPlayerFactory.newSimpleInstance(mContext, new DefaultTrackSelector(), defaultLoadControl);
            mPlayer.setPlayWhenReady(true);
        }
        if (mMediaFile != null) {
            mPlayer.prepare(buildMediaSource(mMediaFile));
        }
    }

    private void loadMediaSource(String url) {
        mMediaFile = Uri.parse(url);
        mPlayer.prepare(buildMediaSource(mMediaFile));
    }

    public void releasePlayer() {
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
    }

    public void playMedia(int position) {
        mPlayer.setPlayWhenReady(true);
        mPosition = position;
        SongNotificationManager.getInstance().createNotification(mPosition, true);
        stateLiveData.setValue(isPlaying());
    }

    public void stopMedia() {
        if (mPlayer == null) return;
        mPlayer.setPlayWhenReady(false);
        stateLiveData.setValue(isPlaying());
    }

    public void pauseMedia() {
        if (isPlaying()) {
            // this mean pause
            mPlayer.setPlayWhenReady(false);
            resumePoint = mPlayer.getCurrentPosition();
            // if the service pause then the notificate will create play notification
            SongNotificationManager.getInstance().createNotification(mPosition, false);
            stateLiveData.setValue(isPlaying());
        }
    }

    public void resumeMedia() {
        if (!isPlaying()) {
            mPlayer.seekTo(resumePoint);
            mPlayer.setPlayWhenReady(true);
            // if the service resume then the notificate will create pause notification
            SongNotificationManager.getInstance().createNotification(mPosition, true);
            stateLiveData.setValue(isPlaying());
        }
    }

    public void seekTo(int progress) {
        resumePoint = progress;
        mPlayer.seekTo(progress);
    }

    public void setMediaFile(String url) {
        loadMediaSource(url);
    }

    private MediaSource buildMediaSource(Uri uri) {
        // build MediaSource from http data source
        DefaultDataSourceFactory dataSource = new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, "Nhaccuato"),
                new DefaultBandwidthMeter());
//        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_media");
        MediaSource mediaSource = new ExtractorMediaSource(uri, dataSource, new DefaultExtractorsFactory(), null, null);
        return mediaSource;
    }

    public boolean isPlaying() {
        return mPlayer.getPlayWhenReady();
    }

    public SimpleExoPlayer getPlayer() {
        return mPlayer;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // the service gained audio focus, so it needs to start playing
                Log.d(TAG, "Audio focus gained");
                mPlayer.setVolume(1.0f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // the service lost audio focus, the user probably moved to playing
                // media on other app, so release the media player
                if (mPlayer != null) {
                    pauseMedia();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Focus lost for a short time, pause the Media player
                if (isPlaying()) pauseMedia();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short of time, lower the volume of the player
                // maybe there is a notification
                if (isPlaying()) mPlayer.setVolume(0.1f);
                break;
        }
    }

    public boolean requestAudioFocus(Context mContext) {
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        Log.d(TAG, "request Audio focus called" + result);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // Focus gained
            this.onAudioFocusChange(AudioManager.AUDIOFOCUS_GAIN);
            return true;
        }
        // Could not gained focus
        return false;
    }

    public boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                mAudioManager.abandonAudioFocus(this);
    }
}