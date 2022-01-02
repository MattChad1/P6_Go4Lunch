package com.go4lunch2.ui.detail_restaurant;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.go4lunch2.R;

public class ReminderBroadcast extends BroadcastReceiver {

    private static final String CANAL = "Noon";

    @Override
    public void onReceive(Context context, Intent intent) {
        String nameRestaurant = intent.getExtras().getString("nameRestaurant");
        String idRestaurant = intent.getExtras().getString("idRestaurant");

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CANAL);
        notificationBuilder.setContentTitle(context.getString(R.string.app_name));
        notificationBuilder.setContentText(context.getString(R.string.noon_goto, nameRestaurant));
        notificationBuilder.setSmallIcon(R.drawable.ic_baseline_notifications_24);
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent intentNotif = new Intent(context, DetailRestaurantActivity.class);
        intentNotif.putExtra(DetailRestaurantActivity.RESTAURANT_SELECTED, idRestaurant);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentNotif, 0);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, notificationBuilder.build());
    }
}
