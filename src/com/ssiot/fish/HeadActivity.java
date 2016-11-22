package com.ssiot.fish;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.remote.LoginActivity;
import com.ssiot.remote.Utils;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.YunNodeModel;
import com.ssiot.remote.yun.webapi.WS_MQTT;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HeadActivity extends ActionBarActivity{
    private static final String tag = "HeadActivity";
    public static int width = 0;
    public static int height = 0;
    protected LayoutInflater mInflater;
    private boolean windowTranslucent = false;
    private boolean hasActionBar = true;
    private boolean viewIsSet = false;
    List<ThreadHolder> threadPool = new ArrayList<ThreadHolder>();
    
    private static final int MSG_TOAST = 898989;
    private Handler mHandler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case MSG_TOAST:
				String str = (String) msg.obj;
				showToast(str);
				break;

			default:
				break;
			}
    		
    	};
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.head));
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.DarkGreen));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mInflater = getLayoutInflater();
        if ((width == 0) || (height == 0)) {
            DisplayMetrics localDisplayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
            width = localDisplayMetrics.widthPixels;
            height = localDisplayMetrics.heightPixels;
        }
        
        if (true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//安卓自定义状态栏颜色以与APP风格保持一致
            windowTranslucent = true;
//            //透明状态栏  
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  
//            //透明导航栏  
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION); 
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);    
            tintManager.setStatusBarTintEnabled(true);    
            tintManager.setStatusBarTintResource(R.color.DarkGreen);//通知栏所需颜色  
          
        }
        /*
        SystemBarTintManager tintManager = new SystemBarTintManager(this);  
        // 激活状态栏设置  
        tintManager.setStatusBarTintEnabled(true);  
        // 激活导航栏设置  
        tintManager.setNavigationBarTintEnabled(true); 
        // 设置一个颜色给系统栏  
        tintManager.setTintColor(Color.parseColor("#99000FF"));  
        // 设置一个样式背景给导航栏  
//        tintManager.setNavigationBarTintResource(R.drawable.my_tint);  
        // 设置一个状态栏资源  
//        tintManager.setStatusBarTintDrawable(MyDrawable);*/
    }
    
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        if (windowTranslucent){
            View rootView = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
            if (hasActionBar){
                rootView.setPadding(0, getActionBarHeight() + getStatusBarHeight(), 0, 0);
            } else {
                rootView.setPadding(0, getStatusBarHeight(), 0, 0);
            }
        }
        viewIsSet = true;
    }
    
    @TargetApi(19)     
    private void setTranslucentStatus(boolean on) {    
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        //当你把窗口flag设置成 FLAG_TRANSLUCENT_STATUS后，你的应用所占的屏幕扩大到全屏，
        //但是最顶上会有背景透明的状态栏，它的文字可能会盖着你的应用的标题栏，你可以手动将你的app显示的内容向下错出一个状态栏的高度，这样就能完成适配了。
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;    
        if (on) {    
            winParams.flags |= bits;    
        } else {    
            winParams.flags &= ~bits;    
        }    
        win.setAttributes(winParams);   
    }
    
    @TargetApi(19)
    private void testbar(){
      //设置应用布局时是否考虑系统窗口布局；如果为true，将调整系统窗口布局以适应你自定义的布局。
        //比如系统有状态栏，应用也有状态栏时。看你这个布局代码，恰恰是在定义标题栏样式，所以用到这行代码了
        getWindow().getDecorView().setFitsSystemWindows(true);
    }
    
    public void hideActionBar(){//must invoke before setcontentview
        Log.v(tag, "----hideActionBar----");
        getSupportActionBar().hide();
        hasActionBar = false;
//        testbar();
        if (viewIsSet){
            Toast.makeText(this, "must invoke before setcontentview", Toast.LENGTH_SHORT).show();
            Log.e(tag, "must invoke before setcontentview");
        }
    }
    
    public void initTitleLeft(int resid){
        TextView titleLeft = (TextView) findViewById(resid);//R.id.title_bar_left
        titleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                View v = getWindow().peekDecorView();
                if (v != null){
                    InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    //// 获取手机状态栏高度  http://www.cnblogs.com/zhengxt/p/3536905.html
    private int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    // 获取ActionBar的高度
    private int getActionBarHeight() {
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))// 如果资源是存在的、有效的
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources()
                    .getDisplayMetrics());
        }
//        Log.v(tag, "-------getActionBarHeight---------=" + actionBarHeight);//MI3 = 144
        return actionBarHeight;
    }
    
    public void showToast(int strRes){
        Toast.makeText(this, strRes, Toast.LENGTH_SHORT).show();
    }
    
    public void showToast(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
    
    public void sendToast(String str){
    	Message msg = mHandler.obtainMessage(MSG_TOAST);
    	msg.obj = str;
    	mHandler.sendMessage(msg);
    }
    
    
    //MQTT 通信-----------------------------------------------------------------------
    
//    public void startGetStatesContinously(List<YunNodeModel> datas,Handler handler){//订阅30秒，30秒后会重新刷新. TODO in better
//        if (null != datas){
//            for (int i = 0; i < datas.size(); i ++){//TODO 如何取消长时间订阅 ，页面不在时？
//                YunNodeModel y = datas.get(i);
//                if (y.nodeType == DeviceBean.TYPE_SENSOR){
//                    new MQTT().subMsg("v1/n/" + datas.get(i).mNodeUnique + "/sensors/r/ack", handler,30);
//                    new MQTT().pubMsg("v1/n/" + datas.get(i).mNodeUnique + "/sensors/r", "{}");//板子状态
//                } else if (y.nodeType == DeviceBean.TYPE_ACTUATOR){
//                    new MQTT().subMsg("v1/n/" + datas.get(i).mNodeUnique + "/switches/r/ack", handler, 30);//状态变了是否会改动
//                    new MQTT().pubMsg("v1/n/" + datas.get(i).mNodeUnique + "/switches/r", "{}");//控制设备状态
//                }
//            }
//        }
//    }
    
    private class ThreadHolder{
    	private ThreadHolder(Thread t, int nType, String uniq){
    		thread = t;
    		nodeType = nType;
    		unique = uniq;
    	}
    	Thread thread;
    	int nodeType;
    	String unique;
    }
    
    private void deleteDeadThreads(){
    	synchronized (threadPool) {//java.util.ConcurrentModificationException
    		for (int i = 0; i < threadPool.size(); i ++){
    			if (!threadPool.get(i).thread.isAlive()){
        			threadPool.remove(threadPool.get(i));
        			i --;
        		}
    		}
		}
    }
    
    private boolean nodeThreadExists(int nodeType, String unique){
    	for (ThreadHolder t : threadPool){
    		if (t.thread.isAlive() && nodeType == t.nodeType && t.unique == unique){
    			Log.i(tag, "!!-----获取节点"+ unique + "状态的进程正在运行，不需要重复新建线程");
    			return true;
    		}
    	}
    	return false;
    }
    
    public void startGetStates(final List<YunNodeModel> datas, final Handler handler){
        if (null != datas){
            for (int i = 0; i < datas.size(); i ++){
                final YunNodeModel y = datas.get(i);
                if (y.nodeType == DeviceBean.TYPE_SENSOR){
                	Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							String jsonStr = new WS_MQTT().GetOneNodeData(y.mNodeUnique);
							parseSensorStateJSON(y.mNodeUnique, jsonStr, datas);
							Message m = handler.obtainMessage(WS_MQTT.MSG_MQTT_GET);
				            m.obj = jsonStr;//topic + "###" + mqttMessage.toString();//不需要在hander中了，handler只要更新ui就可以了//TODO
				            handler.sendMessage(m);
						}
					});
                	deleteDeadThreads();//这个ThreadHolder设计出来是为了减少新建很多线程
                	if (!nodeThreadExists(y.nodeType, y.mNodeUnique)){
                		threadPool.add(new ThreadHolder(t, y.nodeType, y.mNodeUnique));
                		t.start();
                	}
//                	t.start();
                } else if (y.nodeType == DeviceBean.TYPE_ACTUATOR){
                	Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							String jsonStr = new WS_MQTT().GetOneNodeDevicesState(y.mNodeUnique);
							parseCtrStateJSON(y.mNodeUnique, jsonStr, datas);
							Message m = handler.obtainMessage(WS_MQTT.MSG_MQTT_GET);
//	                        m.obj = jsonStr;//topic + "###" + mqttMessage.toString();//不需要在hander中了，handler只要更新ui就可以了//TODO
	                        handler.sendMessage(m);
						}
					});
                	deleteDeadThreads();//这个ThreadHolder设计出来是为了减少新建很多线程
                	if (!nodeThreadExists(y.nodeType, y.mNodeUnique)){
                		threadPool.add(new ThreadHolder(t, y.nodeType, y.mNodeUnique));
                		t.start();
                	}
//                	t.start();
                	
//                	new MQTT().subMsg("v1/n/" + datas.get(i).mNodeUnique + "/switches/r/ack", handler);
//                    new MQTT().pubMsg("v1/n/" + datas.get(i).mNodeUnique + "/switches/r", "{}");//控制设备状态
                }
            }
        }
    }
	
    public void parseSensorStateJSON(String nodeUnique, String str,List<YunNodeModel> datas){
    	try {
    		if (TextUtils.isEmpty(str)){
    			return;
    		}
    		JSONObject interfaceJson = new JSONObject(str);
	    	JSONObject jo = new JSONObject(interfaceJson.getString("data"));
	    	YunNodeModel currentModel = null;
	    	if (null != datas){
	    		for (int k = 0; k < datas.size(); k ++){
	                YunNodeModel tmp = datas.get(k);
	                if (tmp.nodeType == DeviceBean.TYPE_SENSOR && tmp.mNodeUnique.equals(nodeUnique)){
	                    currentModel = datas.get(k);
	                    break;
	                }
	            }
	    	}
	    	if (null != currentModel){
	    		int rtc = jo.getInt("rtc");
	    		int interfacertc = interfaceJson.getInt("time");
	    		if (rtc <= 0){
	    			rtc = interfacertc;
	    		}
//	    		currentModel.mLastTime = new Timestamp((long) interfacertc * 1000);//王规划的json在time
	    		Iterator it = jo.keys();
	        	while(it.hasNext()){
	        		String key = (String) it.next();
	                String v = jo.getString(key);
	                int underlineIndex = key.indexOf("_");
	                if (underlineIndex < 0){
	                	if ("rtc".equals(key) && rtc > 0){
	                		currentModel.mLastTime = new Timestamp((long)rtc * 1000);
	                	}
	                	continue;
	                }
	                int sen = Integer.parseInt(key.substring(0, underlineIndex));
	                int ch = Integer.parseInt(key.substring(underlineIndex + 1, key.length()));
//	                Log.v(tag, "---------sen:" + sen + " ch:" + ch);
	                boolean atLeastOneOnLine = false;//至少有一个在线，让虚拟传感器的在线状态跟随真传感器的在线状态20161104
	                for (int z = 0; z < currentModel.list.size(); z ++){
	                	DeviceBean d = currentModel.list.get(z);
	                	if (d.mDeviceTypeNo == sen && d.mChannel == ch){
	                		d.valueStr = v;//保存数值字符串
	                		d.value = (float) Float.parseFloat(v);//此值对第一个界面没有用
	                		d.mTime = new Timestamp(((long)rtc * 1000));
	                		d.status = 0;
	                		atLeastOneOnLine = true;
	                	}
	                }
//	                for (int n = 0; n < currentModel.list.size(); n ++){//20161104虚拟传感器 有bug 都在线了！！TODO
//	                	DeviceBean d = currentModel.list.get(n);
//	                	if (atLeastOneOnLine && d.mTime == null){
//	                		d.mTime = new Timestamp(((long)rtc * 1000));
//	                		d.status = 0;
//	                	}
//	                }
	        	}
	    	}
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void parseCtrStateJSON(String nodeUnique, String str,List<YunNodeModel> datas){
    	try {
	    	JSONObject jo = new JSONObject(str);
	    	YunNodeModel currentModel = null;
	    	if (null != datas){
	    		for (int k = 0; k < datas.size(); k ++){
	                YunNodeModel tmp = datas.get(k);
	                if (tmp.nodeType == DeviceBean.TYPE_ACTUATOR && tmp.mNodeUnique.equals(nodeUnique)){
	                    currentModel = datas.get(k);
	                    break;
	                }
	            }
	    	}
	    	if (null != currentModel){
	    		String values = jo.getString("values");
//        		int status = jo.getInt("status");
        		JSONArray jarray = jo.getJSONArray("values");
        		for (int y = 0; y < jarray.length(); y ++){
        			int onoff = jarray.getInt(y);
        			for (int z = 0; z < currentModel.list.size(); z ++){
            			DeviceBean d = currentModel.list.get(z);
            			if (d.mChannel == (y + 1)){
            				d.status = onoff;
//            				Log.v(tag, "-----------ctrl  no:" + d.mChannel + " status:" + onoff);
            				break;
            			}
            		}
        		}
	    	}
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
}