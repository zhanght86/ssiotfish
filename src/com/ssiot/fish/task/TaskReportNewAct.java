package com.ssiot.fish.task;

import android.os.Bundle;

import com.ssiot.fish.HeadActivity;
import com.ssiot.fish.R;

public class TaskReportNewAct extends HeadActivity{
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    private void initView(){
        setContentView(R.layout.task_new_report);
    }
}