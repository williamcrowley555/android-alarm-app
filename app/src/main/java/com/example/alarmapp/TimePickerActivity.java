package com.example.alarmapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import com.example.alarmapp.model.AlarmTimeModel;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerActivity extends AppCompatActivity {
    Button btnOk, btnRemove;
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
        btnRemove = (Button) findViewById(R.id.btnRemove);
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
        } else {
            btnRemove.setVisibility(View.GONE);
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 00);

                AlarmTimeModel savedAlarmTime = null;
                if (selectedAlarmTimeId == null) {
                    savedAlarmTime = addAlarmTime(hour, minute);
                } else {
                    savedAlarmTime = updateAlarmTime(selectedAlarmTimeId, hour, minute);
                }

                if (savedAlarmTime.getStatus() == 1) {
                    AlarmUtil.turnOnAndRepeat(TimePickerActivity.this, intent, calendar, savedAlarmTime.getId());
                }

                finish();
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedAlarmTimeId != null) {
                    deleteAlarmTime(selectedAlarmTimeId);
                    finish();
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

            selectedAlarmTime.setHour(newHour);
            selectedAlarmTime.setMinute(newMinute);
        }

        return selectedAlarmTime;
    }

    public void deleteAlarmTime(int id) {
        AlarmTimeModel selectedAlarmTime =  findAlarmTimeById(id);

        if (selectedAlarmTime.getId() != null) {
            if (selectedAlarmTime.getStatus() == 1) {
                AlarmUtil.turnOff(TimePickerActivity.this, intent, id);
            }

            database.queryData("DELETE FROM AlarmTime WHERE Id = " + id);
        }
    }

    // Starting in Android 8.0 (API level 26), all notifications must be assigned to a channel
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