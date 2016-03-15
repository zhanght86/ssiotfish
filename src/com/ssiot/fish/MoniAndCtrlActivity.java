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
    
    public static final int MSG_GETVERSION_END = 1;
    public static final int MSG_DOWNLOADING_PREOGRESS = 2;
    public static final int MSG_DOWNLOAD_FINISH = 3;
    public static final int MSG_SHOWERROR = 4;
    public static final int MSG_DOWNLOAD_CANCEL = 5;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_GETVERSION_END:
                    if (msg.arg1 <= 0){//大多是网络问题
                        Intent i = new Intent(SettingFrag.ACTION_SSIOT_UPDATE);
                        i.putExtra("checkresult", 0);
                        sendBroadcast(i);
                    } else if (msg.arg1 > msg.arg2){//remoteversion > curVersion
                        HashMap<String, String> mVerMap = (HashMap<String, String>) msg.obj;
                        showUpdateChoseDialog(mVerMap);
                        
                        Intent i = new Intent(SettingFrag.ACTION_SSIOT_UPDATE);
                        i.putExtra("checkresult", 1);
                        sendBroadcast(i);
                    } else if (msg.arg1 == msg.arg2){
                        Intent i = new Intent(SettingFrag.ACTION_SSIOT_UPDATE);
                        i.putExtra("checkresult", 2);
                        sendBroadcast(i);
                    } else {
                        Toast.makeText(MoniAndCtrlActivity.this, "本地版本高于服务器版本", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MSG_DOWNLOADING_PREOGRESS:
                    Log.v(tag, "-------PREOGRESS----" +msg.arg1 + " " + (null != mNoti));
                    int pro = msg.arg1;
                    if (null != mNoti){
                        NotificationManager mnotiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                        mNoti.contentView.setProgressBar(R.id.noti_progress, 100, pro, false);
//                        mNoti.contentView.setTextViewText(R.id.noti_text, "" + pro);
                        mNoti.setLatestEventInfo(MoniAndCtrlActivity.this, "正在更新", "已下载：" + pro + "%", 
                                PendingIntent.getActivity(MoniAndCtrlActivity.this, -1, new Intent(""), 0));
                        mnotiManager.notify(UpdateManager.NOTIFICATION_FLAG, mNoti);
                    }
                    break;
                case MSG_DOWNLOAD_FINISH:
                    NotificationManager mnotiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mnotiManager.cancel(UpdateManager.NOTIFICATION_FLAG);
                    mUpdaManager.installApk();
                    break;
                case MSG_SHOWERROR:
                    NotificationManager mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mManager.cancel(UpdateManager.NOTIFICATION_FLAG);
                    Toast.makeText(MoniAndCtrlActivity.this, "下载出现错误", Toast.LENGTH_LONG).show();
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
        
        if (mPref.getBoolean(Utils.PREF_AUTOUPDATE, true) == true){
            mUpdaManager = new UpdateManager(this, mHandler);
            mUpdaManager.startGetRemoteVer();
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
            mUpdaManager = new UpdateManager(this, mHandler);
        }
        if (mPref.getBoolean(Utils.PREF_AUTOUPDATE, true) == false){
            Editor editor = mPref.edit();
            editor.putBoolean(Utils.PREF_AUTOUPDATE, true);
            editor.commit();
        }
        mUpdaManager.startGetRemoteVer();
    }
    
    private void showUpdateChoseDialog(HashMap<String, String> mVerMap){
        final HashMap<String, String> tmpMap = mVerMap;
        AlertDialog.Builder builder =new Builder(this);
        builder.setTitle(R.string.soft_update_title);
        builder.setMessage(R.string.soft_update_info);
        builder.setPositiveButton(R.string.soft_update_updatebtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mNoti = mUpdaManager.showNotification(MoniAndCtrlActivity.this);
//                        .setProgressBar(R.id.noti_progress, 100, 0, false);
                mUpdaManager.startDownLoad(tmpMap);
//                showDownloadDialog(tmpMap);
                dialog.dismiss();
                Editor e = mPref.edit();
                e.putBoolean(Utils.PREF_AUTOUPDATE, true);
                e.commit();
                Toast.makeText(MoniAndCtrlActivity.this, "转向后台下载，可在通知栏中查看进度。", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.soft_update_later, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Editor e = mPref.edit();
                e.putBoolean(Utils.PREF_AUTOUPDATE, false);
                e.commit();
                dialog.dismiss();
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }
}