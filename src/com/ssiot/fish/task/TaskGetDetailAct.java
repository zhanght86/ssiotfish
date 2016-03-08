
package com.ssiot.fish.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.fish.HeadActivity;
import com.ssiot.fish.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.business.TaskCenter;
import com.ssiot.remote.data.model.TaskCenterModel;
import com.ssiot.remote.data.model.TaskReportModel;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TaskGetDetailAct extends HeadActivity {
    View infoView;
    TableRow frameRow;
    TextView frameNameTextView;
    TextView sourceTextView;
    TextView turnTextView;
    TextView timeView;
    LinearLayout getLayout;
    TextView stateTextView;
    TextView senderTextView;
    TableLayout infoLayout;
    TextView startTimeTextView;
    TextView endTimeTextView;
    LinearLayout contentLayout;
    TextView contentTextView;
    TextView cTitleTextView;
    TextView reportTextView;
    ListView reportListView;

    TaskCenterModel taskModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskModel = (TaskCenterModel) getIntent().getSerializableExtra("taskcentermodel");
        initView();
        setvalue();
        initTitleBar();
    }

    private void initView() {
        setContentView(R.layout.task_get_detail);
        // initTitleText();
        infoView = mInflater.inflate(R.layout.task_get_detail_top, null);
        // frameRow =
        // ((TableRow)infoView.findViewById(R.id.TableRowFrame));//任务园区
        // frameNameTextView =
        // ((TextView)infoView.findViewById(R.id.textViewFrameName));
        sourceTextView = ((TextView) infoView.findViewById(R.id.textViewSource));// 任务来源
        // appendixLayout =
        // ((LinearLayout)infoView.findViewById(R.id.LinearLayoutAppendix));//附件
        // appendixRow =
        // ((TableRow)infoView.findViewById(R.id.tableRowAppendix));
        turnTextView = ((TextView) infoView.findViewById(R.id.textViewTurn));// 转发
        turnTextView.setOnClickListener(listener);
        // initRec(infoView);//处理接受者的ui ？
        timeView = ((TextView) infoView.findViewById(R.id.textViewDate));// 接收时间
        getLayout = ((LinearLayout) infoView.findViewById(R.id.LinearLayoutGet));// 确认收到
        getLayout.setOnClickListener(listener);
        stateTextView = ((TextView) infoView.findViewById(R.id.textViewState));//
        // moreLayout =
        // ((LinearLayout)infoView.findViewById(R.id.linearLayoutMore));//UI
        // 中重复的？
        senderTextView = ((TextView) infoView.findViewById(R.id.textViewSender));// 发起者
        // moreLayout.setOnClickListener(listener);
        infoLayout = ((TableLayout) infoView.findViewById(R.id.tableLayoutInfo));// 要手动隐藏的下半部
        startTimeTextView = ((TextView) infoView.findViewById(R.id.textViewStartTime));
        endTimeTextView = ((TextView) infoView.findViewById(R.id.textViewEndTime));
        // noTextView = ((TextView)infoView.findViewById(R.id.textViewNo));
        // phoTextView =
        // ((TextView)infoView.findViewById(R.id.textViewPho));//？？？？？
        // dividImageView =
        // ((ImageView)infoView.findViewById(R.id.ImageViewDivier));
        // initExe(infoView);
        // locationTextView =
        // ((TextView)infoView.findViewById(R.id.textViewLocation));
        // fertilizesTextView =
        // ((TextView)infoView.findViewById(R.id.textViewFertilizes));
        // spraysTextView =
        // ((TextView)infoView.findViewById(R.id.textViewSprays));
        // ackCycleTextView =
        // ((TextView)infoView.findViewById(R.id.textViewAckCycle));
        // scaleTextView =
        // ((TextView)infoView.findViewById(R.id.textViewExecScale));//汇报时间
        contentLayout = ((LinearLayout) infoView.findViewById(R.id.linearLayoutContent));// 即详细
        contentLayout.setOnClickListener(listener);
        contentTextView = ((TextView) infoView.findViewById(R.id.textViewContent));
        reportListView = ((ListView) findViewById(R.id.listViewReport));// PullHeaderListView
                                                                        // TODO
        cTitleTextView = ((TextView) infoView.findViewById(R.id.textViewContentTitle));// 汇报
                                                                                       // 无汇报
        reportTextView = ((TextView) findViewById(R.id.textViewReport));// 写汇报按钮
        reportTextView.setOnClickListener(listener);

        reportListView.addHeaderView(infoView);
    }

    private void setvalue() {
        if (taskModel._state < 2){
            getLayout.setVisibility(View.VISIBLE);
        } else {
            getLayout.setVisibility(View.GONE);
        }
        sourceTextView.setText(""+taskModel._userid);
        TextView mReciverView = (TextView) infoView.findViewById(R.id.textViewReFirstExe);
        mReciverView.setText(taskModel._tousers);
        startTimeTextView.setText(formatTime(taskModel._starttime));
        endTimeTextView.setText(formatTime(taskModel._endtime));
        contentTextView.setText(taskModel._contenttext);
        
        ArrayList<TaskReportModel> datas = new ArrayList<TaskReportModel>();
        TaskReportAdapter adapter = new TaskReportAdapter(TaskGetDetailAct.this, datas);
        reportListView.setAdapter(adapter);
    }
    
    private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        if (taskModel._state < 2){
            titleRight.setText("请queren");//TODO
        } else {
            titleRight.setText("写汇报");
        }
        
//        titleRight.setBackgroundResource(R.id.)
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskGetDetailAct.this, TaskReportNewAct.class);
                startActivity(intent);
            }
        });
        TextView titleLeft = (TextView) findViewById(R.id.title_bar_left);
        titleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    private String formatTime(Timestamp ts){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d = new Date(ts.getTime());
        return formatter.format(d);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.LinearLayoutGet://queren 收到
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            new TaskCenter().UpdateState(taskModel._id, 2);
                        }
                    }).start();
                    
                    break;

                default:
                    break;
            }
        }
    };
}
