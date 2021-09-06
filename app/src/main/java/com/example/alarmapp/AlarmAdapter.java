package com.example.alarmapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.example.alarmapp.model.AlarmTimeModel;

import java.util.List;

public class AlarmAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<AlarmTimeModel> alarmTimeList;

    public AlarmAdapter(Context context, int layout, List<AlarmTimeModel> alarmTimeList) {
        this.context = context;
        this.layout = layout;
        this.alarmTimeList = alarmTimeList;
    }

    @Override
    public int getCount() {
        return alarmTimeList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView txtTime;
        Switch switchTime;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            holder.txtTime = (TextView) convertView.findViewById(R.id.txt_time);
            holder.switchTime = (Switch) convertView.findViewById(R.id.switchTime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AlarmTimeModel alarmTime = alarmTimeList.get(position);

        int hour = alarmTime.getHour() > 12 ? (alarmTime.getHour() - 12) : alarmTime.getHour();
        int minute = alarmTime.getMinute();

        String period = alarmTime.getHour() > 12 ? "PM" : "AM";
        String strHour = hour > 9 ? String.valueOf(hour) : "0" + hour;
        String strMinute = minute > 9 ? String.valueOf(minute) : "0" + minute;

        holder.txtTime.setText(strHour + ":" + strMinute + " " + period);
        holder.switchTime.setChecked(alarmTime.getStatus() == 0 ? false : true);

        return convertView;
    }
}
