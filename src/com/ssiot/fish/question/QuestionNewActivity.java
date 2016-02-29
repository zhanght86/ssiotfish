package com.ssiot.fish.question;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.fish.R;
import com.ssiot.remote.SsiotConfig;
import com.ssiot.remote.data.business.Question;
import com.ssiot.remote.data.model.QuestionModel;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import com.ssiot.remote.Utils;

public class QuestionNewActivity extends Activity{
    private static final String tag = "QuestionNewActivity";
    TextView titleLeft;
    TextView titleRight;
    EditText questionTitle;
    EditText questionContent;
    GridView mGridView;
    TextView locationTextView;
    ArrayList<Bitmap> picImgs = new ArrayList<Bitmap>();
    PicGridAdapter picAdapter;
    File sdcardTempFile = new File("/mnt/sdcard/", "unkonw.jpg");
    Location mLocation;
    String addr = "";
    SharedPreferences mPref;
    
    private static final int MSG_ADD_END = 1;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_ADD_END:
                    if (msg.arg1 > 0){
                        finish();
                    } else {
                        Toast.makeText(QuestionNewActivity.this, "error", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_submit_question_extra);
        mPref = PreferenceManager.getDefaultSharedPreferences(QuestionNewActivity.this);
//        if (getActionBar() != null){
//            getActionBar().hide();
//        }
        
        titleLeft = (TextView) findViewById(R.id.title_bar_left);
        titleRight = (TextView) findViewById(R.id.title_bar_right);
        questionTitle = (EditText) findViewById(R.id.submit_question_title_edittext);
        questionContent = (EditText) findViewById(R.id.submit_question_extra_edittext);
        mGridView = (GridView) findViewById(R.id.submit_question_camera);
        locationTextView = (TextView) findViewById(R.id.submit_question_pos);
        picAdapter = new PicGridAdapter(this, picImgs);
        mGridView.setAdapter(picAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < picImgs.size()){
                    
                } else {
                    startCameraForResult();
                }
            }
        });
        //TODO
        titleLeft.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        titleRight.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                final QuestionModel model = new QuestionModel();
                model._userId = mPref.getInt(Utils.PREF_USERID, 0);
                model._title = questionTitle.getText().toString();
                model._contentText = questionContent.getText().toString();
                model._createTime = new Timestamp(System.currentTimeMillis());
                if (null != mLocation){
                    model._addr = addr;
                    model._longitude = (float) mLocation.getLongitude();
                    model._latitude = (float) mLocation.getLatitude();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int ret = new Question().Add(model);
                        Message m = mHandler.obtainMessage(MSG_ADD_END);
                        m.arg1 = ret;
                        mHandler.sendMessage(m);
                    }
                }).start();
                
            }
        });
        
        mLocation = getLocation();
        if (null != mLocation){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    addr = getAddrFromBaidu(mLocation);
                    locationTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            locationTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_submit_lbs_selected, 0, R.drawable.icon_arrow_grey, 0);
                            locationTextView.setText(addr);
                        }
                    });
                }
            }).start();
        }
    }
    
    private String getAddrFromBaidu(Location lo){
//        try {  
//            
//            //定义地址  
//            URL url=new URL("http://api.map.baidu.com/geocoder/v2/?ak=" +
//            		"GZuY9lvKWfdh9NtoctRiZLUy&callback=renderReverse&location=" + lo.getLongitude() +","+lo.getLatitude()+"&output=json&pois=1");  
//            //打开连接  
//            HttpURLConnection http=(HttpURLConnection)url.openConnection();  
//            //得到连接状态  
//            int nRC=http.getResponseCode();  
//            if(nRC == 0) {
//                //取得数据  
//                InputStream is = http.getInputStream();  
//                char[] buff = new char[1024];
//                byte[] buf = new;
//                is.rea
//                String readStr = new String(buff);
//                  
//                //http://blog.csdn.net/hewence1/article/details/46324799       HttpResponse TODO
//            }  
            
            
        try {
            //此百度的ak类别是选择的服务端
            URL url = new URL("http://api.map.baidu.com/geocoder/v2/?ak=" +
                    "nX6MZuMH44lfg5jHQ3smcchQ&callback=renderReverse&location=" + lo.getLatitude()
                    + "," + lo.getLongitude() + "&output=json&pois=0");
            HttpGet httpRequest = new HttpGet(url.toString());
            HttpClient httpclient = new DefaultHttpClient();
            // 请求HttpClient，取得HttpResponse
            HttpResponse httpResponse = httpclient.execute(httpRequest);
            // 请求成功
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 取得返回的字符串
                String strResult = EntityUtils.toString(httpResponse.getEntity());
                Log.v(tag, strResult + " " +lo.getLongitude() + " "+lo.getLatitude());
                return parseLocationJSONStr(strResult);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    private String parseLocationJSONStr(String str){
        String str2 = str;
        str2 = str2.substring(str2.indexOf("{"), str2.lastIndexOf("}")+1);
        Log.v(tag,  "----parseLocationJSONStr----"+str2);
        try {
            JSONObject jo = new JSONObject(str2);
            if (jo.getInt("status") == 0){
                JSONObject jResult = jo.getJSONObject("result");
                String addr = jResult.getString("formatted_address");
                return addr;
            } else {
                Log.e(tag, "error");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
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
//                    imgView.setImageBitmap(bm);
                    picImgs.add(bm);
                    if (null != picAdapter){
                        picAdapter.notifyDataSetChanged();
                    }
                    String spath = Environment.getExternalStorageDirectory()+ "/"+SsiotConfig.CACHE_DIR +"/"+ 
                            "yuguanjia/question/uploadimg/" + System.currentTimeMillis() + ".jpg";
                    saveImage(bm, spath);
//                    uploadimg();//TODO
                }
            }
        }
    }
    
    public boolean saveImage(Bitmap photo, String spath) {  
        try {
            File f = new File(spath);
            if (!f.exists()){
                if (!f.getParentFile().exists()){
                    f.getParentFile().mkdirs();
                }
            }
            new File(spath).createNewFile();
            BufferedOutputStream bos = new BufferedOutputStream(  
                    new FileOutputStream(spath, false));  
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);  
            bos.flush();  
            bos.close();  
        } catch (Exception e) {
            e.printStackTrace();
            return false;  
        }  
        return true;  
    }
    
    private Location getLocation(){
        LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();  
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//高精度  
        criteria.setAltitudeRequired(false);//无海拔要求  
        criteria.setBearingRequired(false);//无方位要求  
        criteria.setCostAllowed(false);//允许产生资费  
        criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗  
        
        // 获取最佳服务对象  
        String provider = locationManager.getBestProvider(criteria,true);
        Location location = locationManager.getLastKnownLocation(provider);
        return location;
    }
}