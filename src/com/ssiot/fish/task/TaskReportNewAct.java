package com.ssiot.fish.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.ssiot.fish.HeadActivity;
import com.ssiot.fish.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.business.TaskCenter;
import com.ssiot.remote.data.business.TaskReport;
import com.ssiot.remote.data.model.TaskCenterModel;
import com.ssiot.remote.data.model.TaskReportModel;

import java.sql.Timestamp;

public class TaskReportNewAct extends HeadActivity{
    TextView textViewSend;
    TextView textViewAddPho;
    TextView textViewLocation;
    LinearLayout linearLayoutLocation;
    EditText editTextContent;
    TextView textViewTask;
    ToggleButton toggleButtonTask;
    int taskid = 0;
    TaskCenterModel taskModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskModel = (TaskCenterModel) getIntent().getSerializableExtra("taskcentermodel");
        taskid = taskModel._id;
        initView();
        initTitleBar();
    }
    
    private void initView(){
        setContentView(R.layout.task_new_report);
        textViewSend = (TextView) findViewById(R.id.textViewSend);
        textViewAddPho = (TextView) findViewById(R.id.textViewAddPho);
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        linearLayoutLocation = (LinearLayout) findViewById(R.id.linearLayoutLocation);
        if (taskModel._needlocation){
            linearLayoutLocation.setVisibility(View.VISIBLE);
            initLBS();
        }
        editTextContent = (EditText) findViewById(R.id.editTextContent);
        textViewTask = (TextView) findViewById(R.id.textViewTask);
        toggleButtonTask = (ToggleButton) findViewById(R.id.toggleButtonTask);
        toggleButtonTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    textViewTask.setText("是");
                } else {
                    textViewTask.setText("不");
                }
            }
        });
    }
    
    private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        
        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TaskReportModel model = new TaskReportModel();
                model._taskid = taskid;
                model._userid = Utils.getIntPref(Utils.PREF_USERID, TaskReportNewAct.this);
                model._contenttext = editTextContent.getText().toString();
                model._location = textViewLocation.getText().toString();
                model._createtime = new Timestamp(System.currentTimeMillis());
                model._isfinish = toggleButtonTask.isChecked();
                if (taskid != 0 && model._userid != 0){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean ret1 = new TaskReport().Add(model) > 0;
                            boolean ret2 = new TaskCenter().UpdateState(taskid, 3);
                            if (ret1 && ret2){
                                setResult(RESULT_OK);
                                finish();
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(TaskReportNewAct.this, "信息填写有误", Toast.LENGTH_SHORT).show();
                }
            }
        };
        textViewSend.setOnClickListener(l);
        titleRight.setOnClickListener(l);
        
        TextView titleLeft = (TextView) findViewById(R.id.title_bar_left);
        titleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    private void initLBS() {
        SDKInitializer.initialize(getApplicationContext());//百度
        LocationClientOption mOption = new LocationClientOption();
        mOption.setOpenGps(true);
        mOption.setCoorType("gcj02");
        mOption.setAddrType("all");
        mOption.setScanSpan(100);
        LocationClient mClient = new LocationClient(this, mOption);
        mClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation paramBDLocation) {
                // TODO Auto-generated method stub
                String mLocation = paramBDLocation.getAddrStr();
                // mLatitude = paramBDLocation.getLatitude();
                // mLongitude = paramBDLocation.getLongitude();
                if (!TextUtils.isEmpty(mLocation)) {
                    textViewLocation.setText(mLocation);
                }
            }
        });
        mClient.start();
        mClient.requestLocation();
    }
}