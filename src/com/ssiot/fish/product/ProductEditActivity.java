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
import com.ssiot.remote.data.business.FishProduction;
import com.ssiot.remote.data.model.FishProductionModel;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProductEditActivity extends HeadActivity{
    private static final String tag = "ProductEditActivity";
    private boolean isProductIn = true;
    
    private int mProductType = -1;//4==其他
    private TextView mTxtType;
    private EditText mNameEdit;
    private EditText mAmountEdit;
    private EditText mPriceEdit;
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
        hideActionBar();
        isProductIn = getIntent().getBooleanExtra("isproductin", true);
        setContentView(R.layout.activity_product_new);
        mPref = PreferenceManager.getDefaultSharedPreferences(ProductEditActivity.this);
        initViews();
        initTitleBar();
    }
    
    private void initViews(){
        mTxtType = (TextView) findViewById(R.id.txt_type);
        mNameEdit = (EditText) findViewById(R.id.edit_name);
        mAmountEdit = (EditText) findViewById(R.id.edit_amount);
        mPriceEdit = (EditText) findViewById(R.id.edit_price);
        mDetailEdit = (EditText) findViewById(R.id.edit_detail);
        mWorkerEdit = (EditText) findViewById(R.id.edit_worker);
        mTxtTime = (TextView) findViewById(R.id.txt_time);
    }
    
    private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FishProductionModel model = new FishProductionModel();
                model._userid = mPref.getInt(Utils.PREF_USERID, 0);
                model._productiontype = mProductType;
                model._name = mNameEdit.getText().toString();
                model._amount = mAmountEdit.getText().toString();
                try{
                    float f = Float.parseFloat(mPriceEdit.getText().toString());
                    model._totalprice = f;
                }catch(Exception e){
                    e.printStackTrace();
                }
                model._detail = mDetailEdit.getText().toString();
                model._worker = mWorkerEdit.getText().toString();
                model._isProductIn = isProductIn;
                model._actiontime = mTimeStamp;
                if (!TextUtils.isEmpty(model._name) && model._userid > 0 && null != mTimeStamp && mProductType > 0 & mProductType < 5){
                    new Thread(new Runnable() {
                        public void run() {
                            int ret = new FishProduction().Add(model);
                            if (ret > 0){
                                mHandler.sendEmptyMessage(MSG_ADD_END);
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(ProductEditActivity.this, "请填写完整", Toast.LENGTH_SHORT).show();
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
        titleView.setText(isProductIn ? "入库" : "出库");
    }
    
    public void ClickFunc(View v) {
        Log.v(tag, "--------ClickFunc-------" );
        switch (v.getId()) {
            case R.id.row_producttype:
                showTypePickDialog();
                break;
            case R.id.row_time:
                showDatePickerDialog();
                break;

            default:
                break;
        }
    }
    
    private void showTypePickDialog(){
        final String[] types = {"鱼苗", "鱼药", "饲料" ,"其他"};
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
//        View view = getLayoutInflater().inflate(R.layout.dia_date_time_pick, null);
//        final DatePicker dp = (DatePicker) view.findViewById(R.id.date_pick);
//        final TimePicker tp = (TimePicker) view.findViewById(R.id.time_pick);
        bui.setSingleChoiceItems(types, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mProductType = which + 1;
                if (mProductType > 0 && mProductType < 5){
                    mTxtType.setText(""+ types[mProductType - 1]);
                }
            }
        });
        bui.setTitle("产品类别").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
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