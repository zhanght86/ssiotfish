package com.ssiot.fish;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.ssiot.remote.SettingFrag;
import com.ssiot.remote.SettingFrag.FSettingBtnClickListener;
import com.ssiot.remote.BaseFragment;
import com.ssiot.remote.SsiotService;
import com.ssiot.remote.UpdateManager;
import com.ssiot.remote.Utils;
import com.ssiot.remote.monitor.HeaderTabFrag;

import java.util.HashMap;
import java.util.List;

public class MoniAndCtrlActivity extends HeadActivity implements FSettingBtnClickListener{
    private static final String tag = "MoniAndCtrlActivity";
    private SharedPreferences mPref;
    private int defaulttabInt = 1;
    private UpdateManager mUpdaManager;
    private Notification mNoti;
    private final static String TAG_HEADER_TAB = "tag_header_tab";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        hideActionBar();
        setContentView(R.layout.activity_main);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        defaulttabInt = getIntent().getIntExtra("defaulttab", 1);
        startService(new Intent(this, SsiotService.class));
        
        if (savedInstanceState == null) {//默认的savedInstanceState会存储一些数据，包括Fragment的实例
//            MainFragment mMainFragment = new MainFragment();
            Log.v(tag, "---------------fragcount:"+getSupportFragmentManager().getBackStackEntryCount());
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, mMainFragment)
//                    .commit();
            
            FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
            HeaderTabFrag monitorFragment = new HeaderTabFrag();
            mTransaction.add(R.id.container, monitorFragment);
            Bundle bundle = new Bundle();
            bundle.putString("uniqueid", mPref.getString(Utils.PREF_USERKEY, ""));
            bundle.putInt("defaulttab", defaulttabInt);
            monitorFragment.setArguments(bundle);
            mTransaction.commit();
        } else {
            savedInstanceState.remove("android:support:fragments");//解决getactivity为空的问题？？
            Log.v(tag, "---------------fragcount&&&&&&&&&&:"+getSupportFragmentManager().getBackStackEntryCount());
        }
        
        
    }
    
    public final static int REQUEST_CODE_CTR_CIRCLE = 2;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(tag, "------onActivityResult----" + requestCode +resultCode);
        switch (requestCode) {
//            case REQUEST_CODE_SCAN:
//                if (resultCode == RESULT_OK){
//                    Bundle bundle = data.getExtras();
//                    Log.v(tag, "-------"+bundle.getString("result"));
//                    Bitmap bitmap = (Bitmap) data.getParcelableExtra("bitmap");
//                    
//                    FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
//                    HistoryDetailFragment hisDetailFragment = new HistoryDetailFragment();
//                    mTransaction.replace(R.id.container, hisDetailFragment, TAG_HISTORY_DETAIL);
//                    Bundle bundle2 = new Bundle();
//                    bundle2.putString("title", bundle.getString("result"));
//                    hisDetailFragment.setArguments(bundle);
//                    mTransaction.addToBackStack(null);
//                    mTransaction.commit();
//                }
//                break;
            case REQUEST_CODE_CTR_CIRCLE://一层一层传递给fragment
                Fragment frag = getSupportFragmentManager().findFragmentByTag(TAG_HEADER_TAB);
                if (null != frag){
                    frag.onActivityResult(requestCode, resultCode, data);
                }
                break;

            default:
                break;
        }
    }
    
    @Override
    public void onBackPressed() {
        List<Fragment> frags = getSupportFragmentManager().getFragments();
        for(Fragment f : frags){
            if (f != null && f.isVisible()){
                if (f instanceof BaseFragment && ((BaseFragment) f).canGoback()){
                    ((BaseFragment)f).onMyBackPressed();
                    return;
                }
            }
        }
        super.onBackPressed();
    }
    
    @Override
    public void onFSettingBtnClick() {
        if (mUpdaManager == null){
            mUpdaManager = new UpdateManager(this);
        }
        if (mPref.getBoolean(Utils.PREF_AUTOUPDATE, true) == false){
            Editor editor = mPref.edit();
            editor.putBoolean(Utils.PREF_AUTOUPDATE, true);
            editor.commit();
        }
        mUpdaManager.startGetRemoteVer();
    }
    
}