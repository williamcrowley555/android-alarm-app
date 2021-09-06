package com.example.alarmapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("I'm in Receiver", "Hello");
        String musicRequest = intent.getExtras().getString("musicRequest");

        Intent myIntent = new Intent(context, MusicService.class);
        myIntent.putExtra("musicRequest", musicRequest);
        context.startService(myIntent);
    }
}
