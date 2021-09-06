package com.example.alarmapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MusicService extends Service {
    MediaPlayer mediaPlayer;
    boolean isOn;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("I'm in Service", "Hello");

        String musicRequest = intent.getExtras().getString("musicRequest");

        if (musicRequest.equals("on")) {
            isOn = true;
        } else if (musicRequest.equals("off")) {
            isOn = false;
        }

        if (isOn) {
            mediaPlayer = MediaPlayer.create(this, R.raw.everywhere_i_go);
            mediaPlayer.start();
            isOn = false;
        } else {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        return START_NOT_STICKY;
    }
}
