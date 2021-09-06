package com.example.alarmapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("I'm in Receiver", "Hello");
        Intent myIntent = new Intent(context, MusicService.class);
        context.startService(myIntent);
    }
}
