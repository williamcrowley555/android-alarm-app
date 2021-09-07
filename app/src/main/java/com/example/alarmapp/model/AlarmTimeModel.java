package com.example.alarmapp.model;

public class AlarmTimeModel {
    private Integer id;
    private int hour;
    private int minute;
    private int status;

    public AlarmTimeModel() {
    }

    public AlarmTimeModel(Integer id, int hour, int minute, int status) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
