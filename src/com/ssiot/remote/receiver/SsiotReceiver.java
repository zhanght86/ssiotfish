package com.ssiot.remote.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.ssiot.remote.SsiotService;
import com.ssiot.remote.Utils;

public class SsiotReceiver extends BroadcastReceiver{
    private static final String tag = "SsiotReceiverFish";
    public static final String ACTION_SSIOT_MSG = "com.ssiot.fish.SHOWMSG";
    static final String BOOTACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        
        String action = intent.getAction();
        
        if (ACTION_SSIOT_MSG.equals(action)){
            
            String extraString = intent.getStringExtra("showmsg");
            Log.v(tag, "----onReceive----" +extraString);
            Toast.makeText(context, extraString + "，请检查网络后重试。", Toast.LENGTH_SHORT).show();
        } else if (BOOTACTION.equals(action)){
            if (Utils.getBooleabPref(Utils.PREF_ALARM, context)){
                Intent myintent = new Intent(context, SsiotService.class);
                context.startService(myintent);
            }
        }
    }
}