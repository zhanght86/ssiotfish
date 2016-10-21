package com.ssiot.remote.yun.webapi;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;

public class WS_MQTT extends WebBaseMQTT{
	public static final int MSG_MQTT_GET = 6666;

	private static final String tag = "WS_MQTT";
	
	public String GetOneNodeData(String nodeUnique){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("uniqueid", "" + nodeUnique);
        String txt = exeRetString("GetNodeData.asmx", "GetOneNodeData", params);
        try {//类似{"uniqueid":"10000613","data":{"770_0":"0.000","769_0":"0.000","rtc":"0"},"time":"1476685641"}
			JSONObject jo = new JSONObject(txt);
//			return jo.getString("data");
			return txt;
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	public void GetOneSensorData(String uniqueid, int sensorno, int channel){		
	}
	
	public String GetNodeOneDeviceState(String nodeUnique, int deviceno){
//		HashMap<String, String> params = new HashMap<String, String>();
//        params.put("uniqueid", "" + nodeUnique);
//        params.put("deviceno", "" + deviceno);
//        String txt = exeRetString("GetNodeState.asmx", "GetNodeOneDeviceState", params);
        return null;
	}
	
	public String GetOneNodeDevicesState(String nodeUnique){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("uniqueid", "" + nodeUnique);
        String txt = exeRetString("GetNodeState.asmx", "GetOneNodeDevicesState", params);
        try {//类似	{"uniqueid":"10000613","data":{"values":["0","0","0"]},"time":"1476684115"}
			JSONObject jo = new JSONObject(txt);
			return jo.getString("data");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	
	
	public void SetDevicesStateImmediately(String nodeUnique, String value){
		
	}
	
	public int SetOneDeviceStateImmediately(String nodeUnique, int deviceno, int value){//立即开关单个继电器
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("uniqueid", "" + nodeUnique);
        params.put("deviceno", "" + deviceno);
        params.put("value", "" + value);
        String txt = exeRetString("SetDeviceState.asmx", "SetOneDeviceStateImmediately", params);
        try {//类似	{"uniqueid":"10000613","data":{"status":"0"},"time":"1476686511"}
			JSONObject jo = new JSONObject(txt);
			JSONObject jData = jo.getJSONObject("data");
			return jData.getInt("status");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return -1;
	}
	
	public int SetNodeCalibration(String nodeUnique, int sensor, int channel, String jsonstr){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("uniqueid", "" + nodeUnique);
        params.put("sensor", "" + sensor);
        params.put("channel", "" + channel);
        params.put("jsonstr", "" + jsonstr);//参数类似[{"y":1,"x":2},{"y":4030,"x":400}]
        String txt = exeRetString("SetSensorCalibration.asmx", "SetNodeCalibration", params);
        try {//类似{"uniqueid":"10000613","data":{"status":"0"},"time":"1476689650"}	
			JSONObject jo = new JSONObject(txt);
			JSONObject jData = jo.getJSONObject("data");
			return jData.getInt("status");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return -1;
	}
	
}