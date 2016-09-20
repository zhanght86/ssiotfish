package com.ssiot.remote.yun.unit;

import android.text.TextUtils;
import android.util.Log;

import com.ssiot.remote.data.MobileAPI;

import java.math.BigDecimal;

public class XYStringHolder{
    private static final String tag = "XYStringHolder";
    public String xString;//2016-04-08 14:00:00.0
    String yString;//376ppm
    public float yData;
    public String unit;
    
    public XYStringHolder(String x,String y){
        xString = x;
        yString = y;
        if (!TextUtils.isEmpty(yString)){
            try {
                String yDStr = MobileAPI.getNumberPartFromStr(yString);
//                yData = new BigDecimal(yDStr);
                yData = Float.parseFloat(yDStr);
                unit = yString.substring(yDStr.length(), yString.length());
                Log.v(tag, "------x:" + x + " yDStr:" + yDStr  + " data:" + yData + "  logtime:" + System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}