package com.example.alarmapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.alarmapp.model.AlarmTimeModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton btnAdd;
    ListView lvAlarmTime;
    ArrayList<AlarmTimeModel> arrayAlarmTime;
    AlarmAdapter adapter;

    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvAlarmTime = (ListView) findViewById(R.id.listViewAlarmTime);
        arrayAlarmTime = new ArrayList<>();

        adapter = new AlarmAdapter(this, R.layout.alarm_items, arrayAlarmTime);
        lvAlarmTime.setAdapter(adapter);

        database = new Database(this, "alarm.sqlite", null, 1);
        database.queryData("CREATE TABLE IF NOT EXISTS AlarmTime(Id INTEGER PRIMARY KEY AUTOINCREMENT, Hour INTEGER, Minute INTEGER,  Status INTEGER)");

        getDataAlarmTimeForFirstTime();

        btnAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TimePickerActivity.class);
            startActivity(intent);
        });

        lvAlarmTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer selectedAlarmTimeId = arrayAlarmTime.get(position).getId();
                Intent intent = new Intent(MainActivity.this, TimePickerActivity.class);
                intent.putExtra("selectedAlarmTimeId", selectedAlarmTimeId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getDataAlarmTime();
    }

    private void getDataAlarmTime() {
        if (!arrayAlarmTime.isEmpty()) {
            arrayAlarmTime.clear();
        }

        Cursor alarmTimeList = database.getData("SELECT * FROM AlarmTime");
        while (alarmTimeList.moveToNext()) {
            int id = alarmTimeList.getInt(0);
            int hour = alarmTimeList.getInt(1);
            int minute = alarmTimeList.getInt(2);
            int status = alarmTimeList.getInt(3);
            arrayAlarmTime.add(new AlarmTimeModel(id, hour, minute, status));
        }

        adapter.notifyDataSetChanged();
    }

    private void getDataAlarmTimeForFirstTime() {
        if (!arrayAlarmTime.isEmpty()) {
            arrayAlarmTime.clear();
        }

        Cursor alarmTimeList = database.getData("SELECT * FROM AlarmTime");
        while (alarmTimeList.moveToNext()) {
            int id = alarmTimeList.getInt(0);
            int hour = alarmTimeList.getInt(1);
            int minute = alarmTimeList.getInt(2);
            int status = alarmTimeList.getInt(3);
            arrayAlarmTime.add(new AlarmTimeModel(id, hour, minute, status));

            if (status == 1) {
                Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 00);

                AlarmUtil.turnOnAndRepeat(MainActivity.this, intent, calendar, id);
            }
        }

        adapter.notifyDataSetChanged();
    }

}