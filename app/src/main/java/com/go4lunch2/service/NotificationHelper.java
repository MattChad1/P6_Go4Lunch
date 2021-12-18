package com.go4lunch2.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.go4lunch2.R;
import com.go4lunch2.ui.main_activity.MainActivity;

public class NotificationHelper {


    public static void createNotificationChannel(Context context, String name, String description) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "NoonNotificationChannel";
//            String description = "Channel for the noon notifications";
            NotificationChannel channel = new NotificationChannel("Noon", name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }


    }


    public static void createSampleDataNotification(Context context, String title, String message, String canal) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, canal);
        notificationBuilder.setContentTitle(context.getString(R.string.app_name));
        notificationBuilder.setContentText(message);
        notificationBuilder.setSmallIcon(R.drawable.ic_baseline_notifications_24);
        Intent intentNotif = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentNotif, 0);
        notificationBuilder.setContentIntent(pendingIntent);

//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(1, notificationBuilder.build());
    }



}
