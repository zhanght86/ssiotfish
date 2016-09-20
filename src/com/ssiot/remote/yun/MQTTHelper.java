package com.ssiot.remote.yun;

import java.sql.Timestamp;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;

import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.YunNodeModel;

public class MQTTHelper {
	private static final String tag = "MQTTHelper";
	
	public void startGetStatesContinously(List<YunNodeModel> datas,Handler handler){//订阅30秒，30秒后会重新刷新. TODO in better
        if (null != datas){
            for (int i = 0; i < datas.size(); i ++){//TODO 如何取消长时间订阅 ，页面不在时？
                YunNodeModel y = datas.get(i);
                if (y.nodeType == DeviceBean.TYPE_SENSOR){
                    new MQTT().subMsg("A/" + datas.get(i).mNodeNo, handler,30);
                    new MQTT().pubMsg("B/" + datas.get(i).mNodeNo, "{\"Cmd\":2,\"Type\":3,\"Data\":[{\"V\":1}]}");//板子状态
                } else if (y.nodeType == DeviceBean.TYPE_ACTUATOR){
                    new MQTT().subMsg("A/" + datas.get(i).mNodeNo, handler, 30);//状态变了是否会改动
                    new MQTT().pubMsg("B/" + datas.get(i).mNodeNo, "{\"Cmd\":5,\"Type\":2,\"Data\":0}");//控制设备状态
                }
            }
        }
    }
    
    public void startGetStates(List<YunNodeModel> datas,Handler handler){
        if (null != datas){
            for (int i = 0; i < datas.size(); i ++){
                YunNodeModel y = datas.get(i);
                if (y.nodeType == DeviceBean.TYPE_SENSOR){
                    new MQTT().subMsg("A/" + datas.get(i).mNodeNo, handler);
                    new MQTT().pubMsg("B/" + datas.get(i).mNodeNo, "{\"Cmd\":2,\"Type\":3,\"Data\":[{\"V\":1}]}");//板子状态
                } else if (y.nodeType == DeviceBean.TYPE_ACTUATOR){
                    new MQTT().subMsg("A/" + datas.get(i).mNodeNo, handler);
                    new MQTT().pubMsg("B/" + datas.get(i).mNodeNo, "{\"Cmd\":5,\"Type\":2,\"Data\":0}");//控制设备状态
                }
            }
        }
    }
    
    private void parseJSONStr(String str){
    	String topic = str.substring(0,str.indexOf("###"));
        String mqttmsg = str.substring(str.indexOf("###") + 3, str.length());
        if (topic.endsWith("/sensor/r/ack")){//单个传感器的值
        	
        } else if (topic.endsWith("/sensors/r/ack")){//一个节点下的传感器的值
        	
        } else if (topic.endsWith("/switch/r/ack")){//单个继电器的状态
        	
        } else if (topic.endsWith("/switches/r/ack")){//一组继电器的状态
        	
        } else if (topic.endsWith("/switch/w/ack")){//操作一个继电器的结果
        	
        } else if (topic.endsWith("/switches/w/ack")){//操作一组继电器的结果
        	
        }
    }
    
    public void parseJSON_GetStates(String str,List<YunNodeModel> datas){
        try {
            String topic = str.substring(0,str.indexOf("###"));
            String mqttmsg = str.substring(str.indexOf("###") + 3, str.length());
            
            JSONObject jo = new JSONObject(mqttmsg);
            int cmd = jo.getInt("Cmd");
            int type = jo.getInt("Type");
            
            YunNodeModel currentModel = null;
            if (null != datas){
                for (int k = 0; k < datas.size(); k ++){
                    YunNodeModel tmp = datas.get(k);
                    boolean typeMatch = false;
                    typeMatch = ((tmp.nodeType == DeviceBean.TYPE_SENSOR) && (cmd == 2) && (type == 1))
                            || ((tmp.nodeType == DeviceBean.TYPE_ACTUATOR) && (cmd == 5) && (type == 2));//有时有采集点和控制点hostid一样的情况
                    if (topic.endsWith(""+tmp.mNodeNo) && typeMatch){
                        currentModel = datas.get(k);
                        break;
                    }
                }
            }
            if (null != currentModel){
                JSONArray jDatas = jo.getJSONArray("Data");
                for (int i = 0; i < jDatas.length(); i ++){
                    JSONObject o = jDatas.optJSONObject(i);
                    
                    if (cmd == 2 && type == 1){
                        int sen = o.getInt("sen");
                        int ch = o.getInt("ch");
                        double v = o.getDouble("v");
//                        Log.v(tag, "@@@@@@@@@@@@@@@@2,1@" + sen + " " + ch + currentModel.dumpAllDeviceBeans());
                        for (int n = 0; n < currentModel.list.size(); n ++){
                            DeviceBean d = currentModel.list.get(n);
                            if (d.mDeviceTypeNo == sen && d.mChannel == ch){
                                d.value = (float) v;//此值对第一个界面没有用
                                d.mTime = new Timestamp(System.currentTimeMillis());
                                d.status = 0;
                            }
                        }
                    } else if (cmd == 5 && type == 2){//控制设备状态字符串
                        int deviceNo = o.getInt("Dev");
                        int status = o.getInt("st");
//                        Log.v(tag, "@@@@@@@@@@@@@@@@5,2@" + deviceNo + " " + status + currentModel.dumpAllDeviceBeans());
                        for (int j = 0; j < currentModel.list.size(); j ++){
                            DeviceBean d = currentModel.list.get(j);
                            if (d.mChannel == deviceNo){
                                d.status = status;
                                break;
                            }
                        }
                    }
                }
            } else {
                Log.e(tag, "!!!!!!!!!!! not found " + topic + " " + mqttmsg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
	
}