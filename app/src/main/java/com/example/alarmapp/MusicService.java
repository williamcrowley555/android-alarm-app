package com.example.alarmapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MusicService extends Service {
    MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String musicRequest = intent.getExtras().getString("musicRequest");

        if (musicRequest.equals("on")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.bigroom_never_dies);

            // Run media player in 10 secs
            CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
                public void onTick(long millisUntilFinished) {
                    mediaPlayer.start();
                }

                public void onFinish() {
                    // Code fire after finish
                    mediaPlayer.stop();
                    Integer alarmTimeId = intent.getIntExtra("alarmTimeId", 0);
                    Intent updateIntent = new Intent(getApplicationContext(), MainActivity.class);
                    updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(updateIntent);
                }
            };
            countDownTimer.start();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Music channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }
}
