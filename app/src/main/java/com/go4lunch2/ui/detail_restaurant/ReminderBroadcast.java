package com.go4lunch2.ui.detail_restaurant;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.go4lunch2.R;

public class ReminderBroadcast extends BroadcastReceiver {

    String TAG = "ReminderBroadcast";
    private static final String CANAL = "Noon";

    @Override
    public void onReceive(Context context, Intent intent) {
        String nameRestaurant = intent.getExtras().getString("nameRestaurant");
        String idRestaurant = intent.getExtras().getString("idRestaurant");

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CANAL);
        notificationBuilder.setContentTitle(context.getString(R.string.app_name));
        notificationBuilder.setContentText("Il est midi. Rendez-vous au restaurant " + nameRestaurant);
        notificationBuilder.setSmallIcon(R.drawable.ic_baseline_notifications_24);
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent intentNotif = new Intent(context, DetailRestaurantActivity.class);
        intentNotif.putExtra(DetailRestaurantActivity.RESTAURANT_SELECTED, idRestaurant);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentNotif, 0);
        notificationBuilder.setContentIntent(pendingIntent);

//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

//        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
//            String channelId = context.getString(R.string.notification_channel_id);
//            String channelTitle = context.getString(R.string.notification_channel_title);
//            String channelDescription = context.getString(R.string.notification_channel_desc);
//            NotificationChannel channel = new NotificationChannel(channelId, channelTitle, NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
//            notificationBuilder.setChannelId(channelId);
//        }
        notificationManager.notify(1, notificationBuilder.build());
    }
}
