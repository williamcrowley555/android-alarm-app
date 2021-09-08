package com.example.alarmapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    Intent myIntent;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        // Open the app if user touch on the notification
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "alarm")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Alarm Notification")
                .setContentText("Time to wake up !")
                .setOngoing(true)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(200, builder.build());

        String musicRequest = intent.getExtras().getString("musicRequest");
        myIntent = new Intent(context, MusicService.class);
        myIntent.putExtra("musicRequest", musicRequest);
        Log.e("Music Request", musicRequest);

        // make the service still work even the app is closed
        context.startForegroundService(myIntent);
    }

}
