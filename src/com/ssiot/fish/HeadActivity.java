package com.ssiot.fish;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;

public class HeadActivity extends ActionBarActivity{
    public static int width = 0;
    public static int height = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.head));
        if ((width == 0) || (height == 0)) {
            DisplayMetrics localDisplayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
            width = localDisplayMetrics.widthPixels;
            height = localDisplayMetrics.heightPixels;
        }
    }
}