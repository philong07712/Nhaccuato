package com.example.nhaccuato.play.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.nhaccuato.Utils.Constants;

public class NotificationActionService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent(Constants.TRACK_CODE)
                .putExtra(CreateNotification.ACTION_NAME, intent.getAction()));
    }
}
