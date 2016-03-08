package com.ssiot.fish.product;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ssiot.fish.HeadActivity;
import com.ssiot.fish.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.business.FishDrug;
import com.ssiot.remote.data.business.FishFeed;
import com.ssiot.remote.data.business.FishSmall;
import com.ssiot.remote.data.model.FishDrugModel;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FishEditActivity extends HeadActivity{
    private static final String tag = "ProductEditActivity";
    private String tableName;
    
    private EditText mNameEdit;
    private EditText mAmountEdit;
    private EditText mAreaEdit;
    private EditText mDetailEdit;
    private EditText mWorkerEdit;
    private TextView mTxtTime;
    
    private Timestamp mTimeStamp;
    SharedPreferences mPref;
    
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
        tableName = getIntent().getStringExtra("edittable");
        setContentView(R.layout.activity_fishproduct_new);//TODO xml
        mPref = PreferenceManager.getDefaultSharedPreferences(FishEditActivity.this);
        initViews();
        initTitleBar();
    }
    
    private void initViews(){
        mNameEdit = (EditText) findViewById(R.id.edit_name);
        mAmountEdit = (EditText) findViewById(R.id.edit_amount);
        mAreaEdit = (EditText) findViewById(R.id.edit_area);
        mDetailEdit = (EditText) findViewById(R.id.edit_detail);
        mWorkerEdit = (EditText) findViewById(R.id.edit_worker);
        mTxtTime = (TextView) findViewById(R.id.txt_time);
    }
    
    private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FishDrugModel model = new FishDrugModel();
                model._userid = mPref.getInt(Utils.PREF_USERID, 0);
                model._name = mNameEdit.getText().toString();
                model._amount = mAmountEdit.getText().toString();
                model._area = mAreaEdit.getText().toString();
                model._detailtext = mDetailEdit.getText().toString();
                model._worker = mWorkerEdit.getText().toString();
                model._actiontime = mTimeStamp;
                if (!TextUtils.isEmpty(model._name) && model._userid > 0 && null != mTimeStamp){
                    new Thread(new Runnable() {
                        public void run() {
                            int ret = 0;
                            if (tableName.equals("FishDrug")){
                                ret = new FishDrug().Add(model);
                            } else if (tableName.equals("FishFeed")){
                                ret = new FishFeed().Add(model);
                            } else if (tableName.equals("FishSmall")){
                                ret = new FishSmall().Add(model);
                            }
                            
                            if (ret > 0){
                                mHandler.sendEmptyMessage(MSG_ADD_END);
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(FishEditActivity.this, "请填写完整", Toast.LENGTH_SHORT).show();
                }
            }
        });
        TextView titleLeft = (TextView) findViewById(R.id.title_bar_left);
        titleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onBackPressed();
                finish();
            }
        });
        
        TextView titleView = (TextView) findViewById(R.id.title_bar_title);
        titleView.setText(tableName);
    }
    
    public void ClickFunc(View v) {
        Log.v(tag, "--------ClickFunc-------" );
        switch (v.getId()) {
            case R.id.row_time:
                showDatePickerDialog();
                break;

            default:
                break;
        }
    }
    
    private void showDatePickerDialog(){
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dia_date_time_pick, null);
        final DatePicker dp = (DatePicker) view.findViewById(R.id.date_pick);
        final TimePicker tp = (TimePicker) view.findViewById(R.id.time_pick);
        bui.setTitle("时间选择").setView(view).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d = new Date(dp.getYear()-1900, dp.getMonth(), dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());
                mTimeStamp = new Timestamp(d.getTime());
                String str = formatter.format(d);
                mTxtTime.setText("时间:"+str);
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
    }
    
}