package com.ssiot.fish.task;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ssiot.fish.ContactView;
import com.ssiot.fish.HeadActivity;
import com.ssiot.fish.R;
import com.ssiot.fish.facility.FishpondNewActivity;
import com.ssiot.fish.product.FishEditActivity;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.business.FacilitiesFishpond;
import com.ssiot.remote.data.business.TaskCenter;
import com.ssiot.remote.data.model.FacilitiesFishpondModel;
import com.ssiot.remote.data.model.TaskCenterModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TaskNewActivity extends HeadActivity{
    LinearLayout toUsersLinear;
    LinearLayout mContainerRE;
    EditText mEditTextContent;
    TextView mTextViewStart;
    TextView mTextViewEnd;
    ToggleButton mTogglePho;
    TextView mTextPho;
    ToggleButton mToggleLocation;
    TextView mTextLocation;
    
    private ArrayList<String> toUserNames = new ArrayList<String>();
    private ArrayList<Integer> toUserIDs = new ArrayList<Integer>();
    Timestamp mStartTimestamp;
    Timestamp mEndTimestamp;
    
    private static final int MSG_ADD_END = 1;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_ADD_END:
                    finish();
                    break;
                default:
                    break;
            }
        };
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_new);
        initTitleBar();
        findViews();
    }
    
    private void findViews(){
        toUsersLinear = (LinearLayout) findViewById(R.id.linearLayoutRe);
        mContainerRE = (LinearLayout) findViewById(R.id.LinearLayoutcontainer);
        mEditTextContent = (EditText) findViewById(R.id.editTextContent);
        mTextViewStart = (TextView) findViewById(R.id.TextViewStart);
        mTextViewEnd = (TextView) findViewById(R.id.TextViewEnd);
        mTogglePho = (ToggleButton) findViewById(R.id.toggleButtonPho);
        mToggleLocation = (ToggleButton) findViewById(R.id.toggleButtonLocation);
        mTextPho = (TextView) findViewById(R.id.textViewPho);
        mTextLocation = (TextView) findViewById(R.id.textViewLoc);
        
        mTogglePho.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTextPho.setText(isChecked ? "需要" : "不需要");
            }
        });
        mToggleLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTextLocation.setText(isChecked ? "需要" : "不需要");
            }
        });
    }
    
    private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TaskCenterModel model = new TaskCenterModel();
                model._userid = Utils.getIntPref(Utils.PREF_USERID, TaskNewActivity.this);
                model._tousers = buildToUsersJSON();
                model._contenttext = mEditTextContent.getText().toString();
                model._createtime = new Timestamp(System.currentTimeMillis());
                model._starttime = mStartTimestamp;
                model._endtime = mEndTimestamp;
                model._needimg = mTogglePho.isChecked();
                model._needlocation = mToggleLocation.isChecked();
                model._img = "";
                model._state = 1;//新建
                if (mStartTimestamp != null && null != mEndTimestamp && !TextUtils.isEmpty(model._contenttext)){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int ret = new TaskCenter().Add(model);
                            if (ret > 0){
                                mHandler.sendEmptyMessage(MSG_ADD_END);
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(TaskNewActivity.this, "请填写完整", Toast.LENGTH_SHORT).show();
                }
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
    
    private String buildToUsersJSON(){
        try {
            JSONArray jaArray = new JSONArray();
            for (int i =0; i < toUserIDs.size(); i ++){
                JSONObject jo = new JSONObject();
                jo.put("to", toUserIDs.get(i));
                jaArray.put(jo);
            }
            return jaArray.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    private final  int REQUEST_USERS = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_USERS:
                if (RESULT_OK == resultCode){
                    toUserNames.clear();
                    toUserIDs.clear();
                    mContainerRE.removeAllViews();
                    toUserNames = data.getStringArrayListExtra("extra_names");
                    toUserIDs = data.getIntegerArrayListExtra("extra_userids");
                    if (null != toUserNames){
                        for (int i = 0; i < toUserNames.size(); i ++){
//                            ContactView child = new ContactView(this);
//                            child.setText(toUserNames.get(i));
//                            mContainerRE.addView(child);
                            addRec(toUserNames.get(i), toUserIDs.get(i));
                        }
                    }
                }
                break;

            default:
                break;
        }
    }
    
    public void addRec(String name,int userid){
        ViewGroup contactView = (ViewGroup)LayoutInflater.from(this).inflate(R.layout.msg_new_item, this.mContainerRE, false);
        ((TextView)contactView.findViewById(R.id.TextViewTitle)).setText(name);
        View deleteView = contactView.findViewById(R.id.ImageButtonDelete);
        deleteView.setTag(R.id.tag_first, name);
        deleteView.setTag(R.id.tag_second, userid);
//        deleteView.setTag(R.id.tag_third, contactView);
        deleteView.setOnClickListener(this.deleteClickListener);
        mContainerRE.addView(contactView);
    }
    
    View.OnClickListener deleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int userid = (Integer) v.getTag(R.id.tag_second);
            int index = -1;
            for (int i = 0; i < toUserIDs.size(); i ++){
                if (userid == toUserIDs.get(i)){
                    index = i;
                    break;
                }
            }
            if (index > -1){
                toUserIDs.remove(index);
                toUserNames.remove(index);
                mContainerRE.removeViewAt(index);
            }
        }
    };
    
    public void ClickAdd(View v){
        Intent userCheckIntent = new Intent(this, TaskReceiverAct.class);
        startActivityForResult(userCheckIntent, REQUEST_USERS);
    }
    
    public void ClickStartTime(View v){
        showDatePickerDialog(v);
    }
    
    public void ClickEndTime(View v){
        showDatePickerDialog(v);
    }
    
    private void showDatePickerDialog(final View v){
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dia_date_time_pick, null);
        final DatePicker dp = (DatePicker) view.findViewById(R.id.date_pick);
        final TimePicker tp = (TimePicker) view.findViewById(R.id.time_pick);
        bui.setTitle("时间选择").setView(view).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d = new Date(dp.getYear()-1900, dp.getMonth(), dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());
                String str = formatter.format(d);
                if (v.getId() == R.id.TableRowStartTime){
                    mStartTimestamp = new Timestamp(d.getTime());
                    ((TextView) findViewById(R.id.TextViewStart)).setText(str);
                } else {
                    mEndTimestamp = new Timestamp(d.getTime());
                    ((TextView) findViewById(R.id.TextViewEnd)).setText(str);
                }
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
    }
    
}