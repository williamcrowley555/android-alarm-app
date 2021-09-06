package com.example.alarmapp;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import com.example.alarmapp.model.AlarmTimeModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class TimePickerActivity extends AppCompatActivity {
    Button btnOk, btnCancel;
    TimePicker timePicker;
    Calendar calendar;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    Database database;
    int selectedAlarmTimeId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timepicker);

        btnOk = (Button) findViewById(R.id.btnOk);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        timePicker = (TimePicker) findViewById(R.id.timePicker);

        database = new Database(this, "alarm.sqlite", null, 1);

        //Get current date
        calendar = Calendar.getInstance();
        createNotificationChannel();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        final Intent intent = new Intent(TimePickerActivity.this, AlarmReceiver.class);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                database.queryData("INSERT INTO AlarmTime VALUES(null, " + hour + ", " + minute + ", 0)");

                int savedAlarmTimeId = 0;
                Cursor data = database.getData("SELECT * FROM AlarmTime ORDER BY Id DESC LIMIT 1");
                while (data.moveToNext()) {
                    savedAlarmTimeId = data.getInt(0);
                }

                intent.putExtra("musicRequest", "on");

                // PendingIntent still exists even the app has been existed
                pendingIntent = PendingIntent.getBroadcast(TimePickerActivity.this, savedAlarmTimeId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TimePickerActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                alarmManager.cancel(pendingIntent);
                intent.putExtra("musicRequest", "off");
                sendBroadcast(intent);
            }
        });
    }

    // Tạo channel notification nếu phiên bản 8 trở lên
    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "alarmChannel";
            String description = "Channel for alarm reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("alarm", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }
}