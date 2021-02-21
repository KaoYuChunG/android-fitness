package com.kao.myapplication.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.kao.myapplication.AlarmActivity;
import com.kao.myapplication.R;

public class NotificationPublisher extends BroadcastReceiver {

    public static final String KEY_NOTIFICATION = "key_notification";
    public static final String KEY_NOTIFICATION_ID = "key_notification_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent ii = new Intent(context.getApplicationContext(), AlarmActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, ii, 0);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String message = intent.getStringExtra(KEY_NOTIFICATION);
        int id = intent.getIntExtra(KEY_NOTIFICATION_ID, 0);

        Notification notification = getNotification(message, context, notificationManager, pIntent);
        notificationManager.notify(id, notification);

    }

    //build the notify
    private Notification getNotification(String content, Context context,
                                         NotificationManager manager, PendingIntent intent) {

        Notification.Builder builder =
                new Notification.Builder(context.getApplicationContext())
                        .setContentText(content)
                        .setContentIntent(intent)
                        .setTicker("Alerta")
                        .setAutoCancel(false)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setSmallIcon(R.mipmap.ic_launcher);

        //para seja funciona na version antiga
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "YOUR_CHANNEL_ID";
            NotificationChannel channel =
                    new NotificationChannel(
                            channelId, "Channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        return builder.build();

    }
}
