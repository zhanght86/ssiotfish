package com.ssiot.fish.task;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ssiot.fish.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.TaskReportModel;

import java.util.ArrayList;
import java.util.List;

public class TaskReportAdapter extends BaseAdapter{
    private Context mContext;
    List<TaskReportModel> mDatas;
    LayoutInflater inflater;
    
    public TaskReportAdapter(Context c, List<TaskReportModel> a){
        mContext = c;
        inflater = LayoutInflater.from(c);
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
        ViewHolder holder;
        if (null == convertView){
            holder = new ViewHolder();
            View v = inflater.inflate(R.layout.task_send_report_item_1, null);
            convertView = v;
            holder.mNameView = (TextView) v.findViewById(R.id.report_name);
            holder.mTimeView = (TextView) v.findViewById(R.id.report_time);
            holder.mDetailView = (TextView) v.findViewById(R.id.report_detail);
            holder.mLocationView = (TextView) v.findViewById(R.id.report_location);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TaskReportModel model = mDatas.get(position);
        holder.mNameView.setText(""+model._username);
        holder.mTimeView.setText(Utils.formatTime(model._createtime));
        holder.mDetailView.setText(model._contenttext);
        if (!TextUtils.isEmpty(model._location)){
            holder.mLocationView.setVisibility(View.VISIBLE);
            holder.mLocationView.setText(model._location);
        } else {
            holder.mLocationView.setVisibility(View.GONE);
        }
        return convertView;
    }
    
    private class ViewHolder{
        TextView mNameView;
        TextView mTimeView;
        TextView mDetailView;
        TextView mLocationView;
    }
    
}