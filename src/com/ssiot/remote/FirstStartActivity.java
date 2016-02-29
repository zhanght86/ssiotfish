
package com.ssiot.remote;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.ssiot.fish.FishMainActivity;
import com.ssiot.fish.R;
import com.ssiot.remote.data.business.User;
import com.ssiot.remote.data.model.UserModel;

public class FirstStartActivity extends Activity {
    private final String tag = "FirstStartActivity";
    private String username = "";
    private String password = "";
    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);

        mPref = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onResume() {
        if (mPref != null) {
            username = mPref.getString(Utils.PREF_USERNAME, "");
            password = mPref.getString(Utils.PREF_PWD, "");

            Log.v(tag, "---------preference:"+username + password);
//            username = "tscmy";
//            password = "tscmy12345";
            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                new Thread(new autoLogin()).start();// auto login
            } else {
                startLoginUI();
            }
        } else {
            Log.e(tag, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            startLoginUI();
        }
        super.onResume();
    }

    class autoLogin implements Runnable {

        @Override
        public void run() {
            try {
                if (Utils.isNetworkConnected(FirstStartActivity.this)){
                    List<UserModel> users =  new User().GetModelList(" Account='" + username + "'");
                    if (null != users && users.size() > 0){
                        UserModel model = users.get(0);
                        if (null != password && null != model._userpassword && password.equals(model._userpassword.trim())){
                            String uniqueID = model._uniqueid;
                            MainActivity.AreaID = model._areaid;
                            
                            Intent intent = new Intent(FirstStartActivity.this, FishMainActivity.class);
                            intent.putExtra("userkey", uniqueID);
                            // startActivity(intent);
                            startActivity(intent);
                            finish();
                            return;
                        }
                    }
                } else {
                }
                startLoginUI();
                
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void startLoginUI() {
        Intent intent = new Intent(FirstStartActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
