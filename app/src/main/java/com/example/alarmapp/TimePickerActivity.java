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
    Button btnOk, btnRepeat;
    TimePicker timePicker;
    Calendar calendar;

    Database database;
    Integer selectedAlarmTimeId = null;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timepicker);

        btnOk = (Button) findViewById(R.id.btnOk);
        btnRepeat = (Button) findViewById(R.id.btnRepeat);
        timePicker = (TimePicker) findViewById(R.id.timePicker);

        intent = new Intent(TimePickerActivity.this, AlarmReceiver.class);

        Intent myIntent = getIntent();
        Bundle bundle = myIntent.getExtras();
        if (bundle != null) {
            selectedAlarmTimeId = bundle.getInt("selectedAlarmTimeId");
        }

        database = new Database(this, "alarm.sqlite", null, 1);

        createNotificationChannel();
        //Get current date
        calendar = Calendar.getInstance();

        if (selectedAlarmTimeId != null) {
            AlarmTimeModel alarmTimeModel = findAlarmTimeById(selectedAlarmTimeId);
            timePicker.setCurrentHour(alarmTimeModel.getHour());
            timePicker.setCurrentMinute(alarmTimeModel.getMinute());
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                AlarmTimeModel savedAlarmTime = null;
                if (selectedAlarmTimeId == null) {
                    savedAlarmTime = addAlarmTime(hour, minute);
                } else {
                    savedAlarmTime = updateAlarmTime(selectedAlarmTimeId, hour, minute);
                }

                if (savedAlarmTime.getStatus() == 1) {
                    AlarmUtil.turnOn(TimePickerActivity.this, intent, calendar, savedAlarmTime.getId());
                }
            }
        });

        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmTimeModel selectedAlarmTime = findAlarmTimeById(selectedAlarmTimeId);
                if (selectedAlarmTime.getId() != null) {
                    updateAlarmTimeStatus(selectedAlarmTime, 1);
                    calendar.set(Calendar.HOUR_OF_DAY, selectedAlarmTime.getHour());
                    calendar.set(Calendar.MINUTE, selectedAlarmTime.getMinute());
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    AlarmUtil.turnOnAndRepeat(TimePickerActivity.this, intent, calendar, selectedAlarmTime.getId());
                }
            }
        });
    }

    public AlarmTimeModel findAlarmTimeById(Integer id) {
        AlarmTimeModel alarmTime = new AlarmTimeModel();

        Cursor data = database.getData("SELECT * FROM AlarmTime WHERE Id = " + id);
        if (data.moveToNext()) {
            alarmTime.setId(data.getInt(0));
            alarmTime.setHour(data.getInt(1));
            alarmTime.setMinute(data.getInt(2));
            alarmTime.setStatus(data.getInt(3));
        }

        return alarmTime;
    }

    public AlarmTimeModel findAlarmTimeByTime(int hour, int minute) {
        AlarmTimeModel alarmTime = new AlarmTimeModel();

        Cursor data = database.getData("SELECT * FROM AlarmTime WHERE Hour = " + hour + " AND Minute = " + minute);
        if (data.moveToNext()) {
            alarmTime.setId(data.getInt(0));
            alarmTime.setHour(data.getInt(1));
            alarmTime.setMinute(data.getInt(2));
            alarmTime.setStatus(data.getInt(3));
        }

        return alarmTime;
    }

    public AlarmTimeModel addAlarmTime(int hour, int minute) {
        AlarmTimeModel savedAlarmTime = null;
        AlarmTimeModel existedAlarmTime = findAlarmTimeByTime(hour, minute);

        if (existedAlarmTime.getId() == null) {
            database.queryData("INSERT INTO AlarmTime VALUES(null, " + hour + ", " + minute + ", 1)");
            Cursor data = database.getData("SELECT * FROM AlarmTime ORDER BY Id DESC LIMIT 1");
            while (data.moveToNext()) {
                Integer id = data.getInt(0);
                int hr = data.getInt(1);
                int min = data.getInt(2);
                int status = data.getInt(3);
                savedAlarmTime = new AlarmTimeModel(id, hr, min, status);
            }

        } else {
            database.queryData("UPDATE AlarmTime SET Status = " + 1 + " WHERE Id = " + existedAlarmTime.getId());
            existedAlarmTime.setStatus(1);
            savedAlarmTime = existedAlarmTime;
        }

        return savedAlarmTime;
    }

    public AlarmTimeModel updateAlarmTime(int id, int newHour, int newMinute) {
        AlarmTimeModel selectedAlarmTime =  findAlarmTimeById(id);

        if (selectedAlarmTime.getId() != null) {
            database.queryData("UPDATE AlarmTime SET Hour = " + newHour + " , Minute = " + newMinute + " WHERE Id = " + id);

            if (selectedAlarmTime.getStatus() == 1) {
                AlarmUtil.turnOff(TimePickerActivity.this, intent, id);
            }

            selectedAlarmTime.setHour(newHour);
            selectedAlarmTime.setMinute(newMinute);
        }

        return selectedAlarmTime;
    }

    public void updateAlarmTimeStatus(AlarmTimeModel alarmTimeModel, int status) {
        database.queryData("UPDATE AlarmTime SET Status = " + status + " WHERE Id = " + alarmTimeModel.getId());
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