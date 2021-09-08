package com.example.alarmapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class AlarmUtil {

    public static void turnOnAndRepeat(Context context, Intent intent, Calendar calendar, int alarmTimeId) {
        // Turn off previous alarm first. Use case: update an alarm time that already has status = 1
        turnOff(context, intent, alarmTimeId);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        intent.putExtra("musicRequest", "on");

        // PendingIntent still exists even the app has been existed
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmTimeId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long startUpTime = calendar.getTimeInMillis();
        if (System.currentTimeMillis() > startUpTime) {
            startUpTime = startUpTime + 24*60*60*1000;
        }
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startUpTime, AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static void turnOff(Context context, Intent intent, int alarmTimeId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmTimeId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
    }
}
