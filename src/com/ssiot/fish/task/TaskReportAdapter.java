package com.ssiot.fish.task;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ssiot.remote.data.model.TaskReportModel;

import java.util.ArrayList;

public class TaskReportAdapter extends BaseAdapter{
    private Context mContext;
    ArrayList<TaskReportModel> mDatas;
    
    public TaskReportAdapter(Context c, ArrayList<TaskReportModel> a){
        mContext = c;
        mDatas = a;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO 
        return null;
    }
    
}