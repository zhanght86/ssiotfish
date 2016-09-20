package com.ssiot.remote.yun.webapi;

import android.util.Log;

import com.ssiot.remote.data.model.UserModel;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;  
import javax.xml.parsers.DocumentBuilderFactory;  
import org.w3c.dom.Document;  
import org.w3c.dom.Element;  
import org.w3c.dom.Node;  
import org.w3c.dom.NodeList;  

public class WS_User extends WebBasedb2 {
    private static final String tag = "WS_User";
    private String MethodFile = "User.asmx";

    public void Delete() {

    }
    
    public UserModel GetUserByPsw(String account, String pwd){//TODO
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("strUserCode", "" + account);
        params.put("strPassword", "" + pwd);
        String txt = exeRetString(MethodFile, "GetUserByPsw", params);
        List<UserModel> list = parse(txt);
        if (null != list && list.size() > 0){
        	return list.get(0);
        }
        return null;
    }

    public UserModel GetUserByID(int userid) {//根据id获取一个
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("UserID", "" + userid);
        String txt = exeRetString(MethodFile, "GetUserByUserID", params);
        List<UserModel> list = parse(txt);
        if (null != list && list.size() > 0){
        	return list.get(0);
        }
        return null;
    }
    
    public int getMainUserId(int userid){
    	List<UserModel> list = GetAllGroupUsersByID(userid);
    	if (null != list && list.size() > 0){
    		for (int i = 0; i < list.size(); i ++){
    			if (list.get(i)._parentid == 12){//12是管理员
    				return list.get(i)._userid;
    			}
    		}
    	}
    	return userid;
    }
    
    public List<UserModel> GetReceiverUserNames(List<Integer> ids) {
        List<UserModel> userModels = new ArrayList<UserModel>();
        for (int i = 0; i < ids.size(); i ++){
            UserModel umodel = GetUserByID(ids.get(i));
            if (null != umodel){
                userModels.add(umodel);
            }
        }
        return userModels;
    }
    
    public List<UserModel> GetAllGroupUsersByID(int userid){//本组织下的所有人
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("UserID", "" + userid);
        String txt = exeRetString(MethodFile, "GetUsersByUserID", params);
        return parse(txt);
    }
    
    private List<UserModel> parse(String str) {
        List<UserModel> models = new ArrayList<UserModel>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(str.getBytes());
            Document doc = builder.parse(is);
            Element rootElement = doc.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("ds");
            for (int i = 0; i < items.getLength(); i++) {
                UserModel cType = new UserModel();
                Node item = items.item(i);
                NodeList properties = item.getChildNodes();
                for (int j = 0; j < properties.getLength(); j++) {
                    Node property = properties.item(j);
                    String nodeName = property.getNodeName();
                    if (null == property.getFirstChild()){
                        continue;
                    }
                    String valueStr = property.getFirstChild().getNodeValue();
                    if (nodeName.equals("UserID")) {
                        cType._userid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("Account")){
                        cType._account = valueStr;
                    } else if (nodeName.equals("UserName")){
                        cType._username = valueStr;
                    } else if (nodeName.equals("UniqueID")){
                    	cType._uniqueid = valueStr;
                    } else if (nodeName.equals("AreaID")){
                        cType._areaid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("UserType")){
                        cType._UserType = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("UserDeviceType")){
                        cType._devicetype = Integer.parseInt(valueStr);
                    }
                }
                models.add(cType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (UserModel m : models){
            Log.v(tag, "------id:" +m._userid + " account:" + m._account);
        }
        return models;
    }

}
