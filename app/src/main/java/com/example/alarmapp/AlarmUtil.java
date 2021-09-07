package com.example.alarmapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class AlarmUtil {

    public static void turnOn(Context context, Intent intent, Calendar calendar, int alarmTimeId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        intent.putExtra("musicRequest", "on");

        // PendingIntent still exists even the app has been existed
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmTimeId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static void turnOff(Context context, Intent intent, int alarmTimeId) {
        Log.e("Turn off", "is called");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmTimeId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);

        intent.putExtra("musicRequest", "off");
        context.sendBroadcast(intent);
    }
}
