package com.example.nhaccuato.play.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.nhaccuato.MainActivity;
import com.example.nhaccuato.R;
import com.example.nhaccuato.Utils.Constants;
import com.example.nhaccuato.models.Song;
import com.example.nhaccuato.play.notification.NotificationActionService;

import java.util.SimpleTimeZone;
import java.util.concurrent.ExecutionException;

import static java.lang.System.load;

public class CreateNotification  {
    private static final String TAG = "CreateNotification";

    public static final String ACTION_ID = "Create Notification 1";
    public static final String ACTION_NAME = "action name";

    public static final String ACTION_PREVIOUS = "action previous";
    public static final String ACTION_PLAY = "action play";
    public static final String ACTION_NEXT = "action next";
    public static final String ACTION_DELETE = "action delete";
    public static final String ACTION_START = "action delete";

    // Fields
    Context mContext;
    Song mSong;
    int mPosition;
    int mSize;

    // Previous
    PendingIntent pendingIntentPrevious;
    int drw_previous;
    // Play
    PendingIntent pendingIntentPlay;
    int drw_play;
    // Next
    PendingIntent pendingIntentNext;
    int drw_next;

    PendingIntent pendingIntentDelete;
    int drw_delete;
    // intent when click in the notification then open the app
    PendingIntent notifyPendingIntent;
    public static Notification notification;


    public CreateNotification(Context context, Song song, int drawable, int pos, int size) {
        mContext = context;
        mSong = song;
        mPosition = pos;
        mSize = size;
        drw_play = drawable;
        String url = song.getThumbnail();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    if (song.getThumbnail() != null) {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(url)
                                .placeholder(R.drawable.ic_baseline_music_note_orange)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        create(resource);
                                    }
                                });
                    }
                    else {
                        Bitmap largeImage = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_baseline_music_note_orange);
                        create(largeImage);
                    }

                }
            };
            new Thread(runnable).start();
        }
    }

    private void create(Bitmap largeImage) {
        // This will check what intent user want to do

        createIntentPrevious();
        createIntentPlay();
        createIntentNext();
        createIntentDelete();
        createIntentStart();

        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(mContext, TAG);
        notification = new NotificationCompat.Builder(mContext, ACTION_ID)
                .setSmallIcon(R.drawable.ic_baseline_music_note)
                .setContentTitle(mSong.getNameSong())
                .setContentText(mSong.getNameArtist())
                .setOnlyAlertOnce(true)
                .setLargeIcon(largeImage)
                .setShowWhen(false)
                .setOngoing(true)
                .addAction(drw_previous, "Previous", pendingIntentPrevious)
                .addAction(drw_play, "Play", pendingIntentPlay)
                .addAction(drw_next, "Next", pendingIntentNext)
                .addAction(drw_delete, "Delete", pendingIntentDelete)
                .setContentIntent(notifyPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);
        notificationManagerCompat.notify(Constants.NOTIFICATION_ID, notification);

    }

    private void createIntentPrevious() {
        Intent intentPrevious = new Intent(mContext, NotificationActionService.class)
                .setAction(ACTION_PREVIOUS);
        pendingIntentPrevious = PendingIntent.getBroadcast(mContext, 0,
                intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
        drw_previous = R.drawable.ic_baseline_skip_previous_24;
    }

    private void createIntentPlay() {
        // Intent play
        Intent intentPlay = new Intent(mContext, NotificationActionService.class)
                .setAction(ACTION_PLAY);
        pendingIntentPlay = PendingIntent.getBroadcast(mContext, 0,
                intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    private void createIntentNext() {
        Intent intentNext = new Intent(mContext, NotificationActionService.class)
                .setAction(ACTION_NEXT);
        pendingIntentNext = PendingIntent.getBroadcast(mContext, 0,
                intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
        drw_next = R.drawable.ic_baseline_skip_next_24;
    }

    private void createIntentDelete() {
        Intent intentDelete = new Intent(mContext, NotificationActionService.class)
                .setAction(ACTION_DELETE);
        pendingIntentDelete = PendingIntent.getBroadcast(mContext, 0,
                intentDelete, PendingIntent.FLAG_UPDATE_CURRENT);
        drw_delete = R.drawable.ic_baseline_close_24;
    }

    private void createIntentStart() {
        Intent notifyIntent = new Intent(mContext, MainActivity.class)
                .setAction(ACTION_START);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notifyIntent.setAction(Intent.ACTION_MAIN);
        notifyIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notifyPendingIntent = PendingIntent.getActivity(
                mContext, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );
    }
}
