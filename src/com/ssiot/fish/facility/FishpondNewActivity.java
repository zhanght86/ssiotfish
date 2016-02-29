package com.ssiot.fish.facility;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.fish.HeadActivity;
import com.ssiot.fish.R;
import com.ssiot.remote.data.business.FacilitiesFishpond;
import com.ssiot.remote.data.model.FacilitiesFishpondModel;
import com.ssiot.remote.Utils;

public class FishpondNewActivity extends HeadActivity{
    private static final String tag = "FishpondNewActivity";
    View nameView;
    View addrView;
    TextView longtiView;
    TextView latiView;
    EditText sizeEditText;
    float x;
    float y;
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
        setContentView(R.layout.activity_fishpond_new);
        mPref = PreferenceManager.getDefaultSharedPreferences(FishpondNewActivity.this);
        nameView = findViewById(R.id.edit_name);
        addrView = findViewById(R.id.edit_addr);
        longtiView = (TextView) findViewById(R.id.txt_longti);
        latiView = (TextView) findViewById(R.id.txt_lati);
        sizeEditText = (EditText) findViewById(R.id.edit_size);
        View.OnClickListener cli = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FishpondNewActivity.this, GetLocationActivity.class);
                startActivityForResult(intent, REQUEST_LOCATION);
            }
        };
        longtiView.setOnClickListener(cli);
        latiView.setOnClickListener(cli);
        initTitleBar();
    }
    
    private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sizeStr = sizeEditText.getText().toString();
                final FacilitiesFishpondModel model = new FacilitiesFishpondModel();
                model._name = ((EditText) nameView).getText().toString();
                model._addr = ((EditText) addrView).getText().toString();
                try {
                    model._size = Float.parseFloat(sizeStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                model._parentid = mPref.getInt(Utils.PREF_USERID, 0);
                model._longitude = x;
                model._latitute = y;
                if (!TextUtils.isEmpty(model._name) && !TextUtils.isEmpty(model._addr) && model._parentid > 0){
                    new Thread(new Runnable() {
                        public void run() {
                            int ret = new FacilitiesFishpond().Add(model);
                            if (ret > 0){
                                mHandler.sendEmptyMessage(MSG_ADD_END);
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(FishpondNewActivity.this, "请填写完整", Toast.LENGTH_SHORT).show();
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
    }
    
    
    public void StartTimeClick(View v){
        Log.v(tag, "----StartTimeClick-----");
        //xml代码中写了！！！！！！！！！！！！！！！！！！
    }
    
    private static final int REQUEST_LOCATION = 1;
    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent intent) {
        switch (requestcode) {
            case REQUEST_LOCATION:
                if (RESULT_OK == resultcode) {
                    x = intent.getFloatExtra("resultx", 0);
                    y = intent.getFloatExtra("resulty", 0);
                    longtiView.setText("经度:" + x);
                    latiView.setText("纬度:"+y);
                }
                break;

            default:
                break;
        }
    }
}