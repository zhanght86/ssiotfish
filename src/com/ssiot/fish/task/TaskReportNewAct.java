package com.ssiot.fish.task;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
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
import com.ssiot.fish.UploadFileBaseActivity;
import com.ssiot.fish.question.PicGridAdapter;
import com.ssiot.fish.question.QuestionNewActivity;
import com.ssiot.remote.SsiotConfig;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.business.TaskCenter;
import com.ssiot.remote.data.business.TaskReport;
import com.ssiot.remote.data.model.TaskCenterModel;
import com.ssiot.remote.data.model.TaskReportModel;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;

public class TaskReportNewAct extends UploadFileBaseActivity{
    TextView textViewSend;
    TextView textViewAddPho;
    GridView picGridView;
    PicGridAdapter picAdapter;
    ArrayList<Bitmap> picImgs = new ArrayList<Bitmap>();
    LinearLayout linearLayoutPhoContain;
    TextView textViewLocation;
    LinearLayout linearLayoutLocation;
    EditText editTextContent;
    TextView textViewTask;
    ToggleButton toggleButtonTask;
    int taskid = 0;
    TaskCenterModel taskModel;
    private Dialog mDialog;
    public static final String FTP_TASK_PATH = "yun.ssiot.com/Upload/TaskImg/";
    String picUrls = "";
    
    private static final int MSG_ADD_END = 1;
    private static final int MSG_FTPUPLOAD_END = 3;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_ADD_END:
//                    if (msg.arg1 > 0){
//                        finish();
//                    } else {
//                        Toast.makeText(TaskReportNewAct.this, "error", Toast.LENGTH_SHORT).show();
//                    }
                    break;
                case MSG_FTPUPLOAD_END:
                    String remoteFileName = (String) msg.obj;
                    if (!TextUtils.isEmpty(remoteFileName)){
                        if (!TextUtils.isEmpty(picUrls)){
                            picUrls += "," + remoteFileName;
                        } else {
                            picUrls = remoteFileName;
                        }
                    }
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
        taskModel = (TaskCenterModel) getIntent().getSerializableExtra("taskcentermodel");
        taskid = taskModel._id;
        initView();
        initTitleBar();
    }
    
    private void initView(){
        setContentView(R.layout.task_new_report);
        textViewSend = (TextView) findViewById(R.id.textViewSend);
        textViewAddPho = (TextView) findViewById(R.id.textViewAddPho);
        picGridView = (GridView) findViewById(R.id.report_new_pic_grid);
        linearLayoutPhoContain = (LinearLayout) findViewById(R.id.linearLayoutPhoContain);
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        linearLayoutLocation = (LinearLayout) findViewById(R.id.linearLayoutLocation);
        if (taskModel._needlocation){
            linearLayoutLocation.setVisibility(View.VISIBLE);
            initLBS();
        }
        if (taskModel._needimg){
            linearLayoutPhoContain.setVisibility(View.VISIBLE);
            picAdapter = new PicGridAdapter(this, picImgs);
            picGridView.setAdapter(picAdapter);
            picGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position < picImgs.size()){
        
                    } else {
                        startCameraForResult();
                    }
                }
            });
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
                model._img = picUrls;
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
    
    private void startCameraForResult(){
        //两种方式拍照 http://www.open-open.com/lib/view/open1413072399203.html
        //http://my.oschina.net/onlytwo/blog/71192
//        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(sdcardTempFile));
//        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", 1);// 裁剪框比例
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("outputX", 500);// 输出图片大小
//        intent.putExtra("outputY", 500);
//        startActivityForResult(intent, 101);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }
    
    private static final int REQUEST_CAMERA = 0;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_CAMERA){
                Bundle bundle = data.getExtras();
                Bitmap bm = (Bitmap) bundle.get("data");
                if (bm != null){
                    bm.recycle();
                }
                bm = (Bitmap) data.getExtras().get("data");
                if(bm != null){
                    picImgs.add(bm);
                    
                    if (null != picAdapter){
                        picAdapter.notifyDataSetChanged();
                    }

                    final String fileName = System.currentTimeMillis() + ".jpg";
                    final String spath = Environment.getExternalStorageDirectory()+ "/"+SsiotConfig.CACHE_DIR +"/"+
                            "yuguanjia/questionimg/" + fileName;
                    saveImage(bm, spath);
                    mDialog = Utils.createLoadingDialog(this, "上传中");
                    mDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String remoteFileName = uploadimg(FTP_TASK_PATH, new File(spath), fileName);
                            Message m = mHandler.obtainMessage(MSG_FTPUPLOAD_END);
                            m.obj = remoteFileName;
                            mHandler.sendMessage(m);
                            if (null != mDialog && mDialog.isShowing()){
                                mDialog.dismiss();
                            }
                        }
                    }).start();
                }
            }
        }
    }
}