package com.ssiot.fish;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

public class HeadActivity extends ActionBarActivity{
    public static int width = 0;
    public static int height = 0;
    protected LayoutInflater mInflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.head));
        mInflater = getLayoutInflater();
        if ((width == 0) || (height == 0)) {
            DisplayMetrics localDisplayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
            width = localDisplayMetrics.widthPixels;
            height = localDisplayMetrics.heightPixels;
        }
        
        if (false && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//安卓自定义状态栏颜色以与APP风格保持一致
//            //透明状态栏  
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  
//            //透明导航栏  
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION); 
            setTranslucentStatus(true);    
            SystemBarTintManager tintManager = new SystemBarTintManager(this);    
            tintManager.setStatusBarTintEnabled(true);    
            tintManager.setStatusBarTintResource(R.color.bgColor);//通知栏所需颜色  
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
    
    @TargetApi(19)     
    private void setTranslucentStatus(boolean on) {    
        Window win = getWindow();    
        WindowManager.LayoutParams winParams = win.getAttributes();    
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;    
        if (on) {    
            winParams.flags |= bits;    
        } else {    
            winParams.flags &= ~bits;    
        }    
        win.setAttributes(winParams);    
    }  
}