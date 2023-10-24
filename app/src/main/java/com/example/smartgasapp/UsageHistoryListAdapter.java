package com.example.smartgasapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UsageHistoryListAdapter extends ArrayAdapter<UsageHistoryItem> {
    int mresource;
    private Context mContext;
    public UsageHistoryListAdapter(Context context, int resource, ArrayList<UsageHistoryItem> objects){
        super(context, resource, objects);
        mresource = resource;
        mContext = context;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        String volume = getItem(position).getVolume();
        String time = getItem(position).getTime();
        String percent = getItem(position).getPercent();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mresource,parent,false);

        TextView tvVolume = convertView.findViewById(R.id.volume);
        TextView tvTime = convertView.findViewById(R.id.time);
        TextView tvPercent = convertView.findViewById(R.id.percent);

        if(!percent.equals("") && !percent.isEmpty()){
            int formatPercent =  (int) (Float.parseFloat(percent) * 1000 + 0.5);
            tvPercent.setText(String.valueOf(formatPercent)+" %");
        }
        else{
            tvPercent.setText("");
        }

        if(!time.equals("") && !time.isEmpty()){
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                Date date = inputFormat.parse(time);
                String outputTime = outputFormat.format(date);
                tvTime.setText(outputTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            tvTime.setText("");
        }

        tvVolume.setText(volume+" 公斤");

        return convertView;
    }
}
