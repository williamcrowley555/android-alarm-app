package com.example.alarmapp;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
        //Intent để mở app khi ấn vào notification
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        //Tạo notification với channelId theo tên đã tạo.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "alarm")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Alarm")
                .setContentText("Time to wakup !")
                .setOngoing(true)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(200, builder.build());

        Log.e("ID",  String.valueOf(intent.getIntExtra("alarmTimeId", 0)));
        String musicRequest = intent.getExtras().getString("musicRequest");
        myIntent = new Intent(context, MusicService.class);
        myIntent.putExtra("musicRequest", musicRequest);
        myIntent.putExtra("alarmTimeId", intent.getIntExtra("alarmTimeId", 0));
        Log.e("Music Request", musicRequest);
        // startForeGroundService để chạy khi app đã tắt
        context.startForegroundService(myIntent);
    }

}
