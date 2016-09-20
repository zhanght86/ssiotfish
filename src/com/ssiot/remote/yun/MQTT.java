package com.ssiot.remote.yun;

import android.R.integer;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.ssiot.fish.ContextUtilApp;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Random;

public class MQTT{
    private static final String tag = "MQTT";
    public static final int MSG_MQTT_GET = 6666;
    public static final int MSG_MQTT_PUB_END = 7777;
    public static final int MSG_MQTT_PUB_FAIL = 8888;
//    String broker       = "tcp://192.168.1.101:5883";
//    String clientId     = "jingbotestreceiver";
    static String broker       = "tcp://mq.ssiot.com:1883";//61613
    static String clientId     = "appclient" 
            + Settings.System.getString(ContextUtilApp.getInstance().getContentResolver(), Settings.System.ANDROID_ID); ;
    private static int iii = 0;
    private static int clientidPubInt = 1;
    
    public void subMsg(String topic, Handler h){
        new SubscribeThread(topic, h, 0).start();
    }
    
    public void subMsg(String topic, Handler h, int durationSeconds){
        new SubscribeThread(topic, h, durationSeconds).start();
    }
    
    public void pubMsg(String topic, String message){
        new PublishThread(topic, message).start();
    }
    
    public void pubMsg(String topic, String message, Handler h){
        PublishThread pubThread = new PublishThread(topic, message);
        pubThread.setHandler(h);
        pubThread.start();
    }
    
    //发消息
    public class PublishThread extends Thread {
        String topic;
        MqttMessage message;
        int qos = 0;
        Handler handler;
        MemoryPersistence persistence = new MemoryPersistence();
        
        PublishThread(String topic,String message){
            this.topic = topic;
            this.message = new MqttMessage(message.getBytes());
        }
        
        public void setHandler(Handler h){
            handler = h;
        }
//        private void sendMessage(String topic,String message){
//            this.topic = topic;
//            this.message = new MqttMessage(message.getBytes());
//            run();
//        }
        @Override
        public synchronized void run() {
            try {
                clientidPubInt ++;
                final String cId = "apppub" + new Random().nextInt(99999)+clientidPubInt;
                MqttClient sampleClient = new MqttClient(broker, cId, persistence);
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                connOpts.setKeepAliveInterval(1);
                sampleClient.connect(connOpts);
                message.setQos(qos);
                sampleClient.publish(topic, message);
                Log.v(tag, "Message published(((((((((((((clientid:" + cId + "    topic:" + topic + "   message:" + message.toString());
                sampleClient.disconnect();
                if (null != handler){
                    Message msg = handler.obtainMessage(MSG_MQTT_PUB_END);
                    msg.obj = message.toString();
                    handler.sendMessage(msg);
                }
            }catch(MqttException e) {
                e.printStackTrace();
                if (null != handler){
                    handler.sendEmptyMessage(MSG_MQTT_PUB_FAIL);
                }
            }
        }
    }
    
    //订阅消息
    public class SubscribeThread extends Thread{
        final String topic;
        private Handler handler;
        int seconds;//如果是0 表示收到一次就关闭
        MemoryPersistence persistence = new MemoryPersistence();
        SubscribeThread( String mtopic, Handler h, int waitSeconds){
            this.topic = mtopic;
            handler = h;
            this.seconds = waitSeconds;
        }
        @Override
        public synchronized void run(){
            try {
                iii ++;
                final String clientidStr = "appsub" +new Random().nextInt(99999)+ iii;
                final MqttClient sampleClient = new MqttClient(broker, clientidStr, persistence);
                final MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                System.out.println("Connecting to broker: " + " clientID:"+(clientidStr) + " toppic:" + topic);
                connOpts.setKeepAliveInterval(5);
                sampleClient.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable throwable) {
                        Log.e(tag, "!!!!!!!!!!!!!!!!!!!!SubscribeThread-connectionLost clientid:" + clientidStr + " "+topic);
                        try {
                            sampleClient.connect(connOpts);
                            sampleClient.subscribe(topic);
                        }catch (MqttException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                        Log.v(tag, "messageArrived:)))))))clientid:" + clientidStr+"    topic" + topic + " \n           msg:"+mqttMessage.toString());
                        Message m = handler.obtainMessage(MSG_MQTT_GET);
                        m.obj = topic + "###" + mqttMessage.toString();
                        handler.sendMessage(m);
                        if (0 == seconds){
                            Log.v(tag, "------------disconnect sampleClient quickly");//暂时收到就关
                            sampleClient.setCallback(null);
//                            sampleClient.unsubscribe(topicFilter);
                            sampleClient.disconnect();
                            
                        }
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                        Log.v(tag, "deliveryComplete");
                    }
                });
                sampleClient.connect(connOpts);
                sampleClient.subscribe(topic);
                if (seconds != 0){
                    new DisconnectThread(sampleClient,seconds).start();
                }
                
            } catch(MqttException e) {
                e.printStackTrace();
            }
        }
    }
    
    private class DisconnectThread extends Thread{
        private MqttClient client;
        int seconds = 0;
        public DisconnectThread(MqttClient c,int laterSeconds){
            client = c;
            seconds = laterSeconds;
        }
        @Override
        public void run() {
            try {
                Thread.sleep(seconds * 1000);
                if (client.isConnected()){
                    client.setCallback(null);
                    client.disconnect();
                }
            } catch (Exception e) {
                Log.e(tag, "-----" + client.getClientId());
                e.printStackTrace();
            }
        }
    }
    
    //终端调试  mosquitto_sub -h 120.26.141.14 -p 61613 -t 'B/30000338'
    //mosquitto_pub -h 120.26.141.14 -p 61613  -t 'B/30000338' -m '{"Cmd":2,"Type":1,"Data":[{"V":12}]}'
//    mosquitto_pub -h 120.26.141.14 -p 1883 -t 'v1/n/10000493/sensors/r' -m '{}'
//    mosquitto_pub -h 120.26.141.14 -p 1883 -t 'v1/n/10000493/switches/r' -m '{}'
    
}