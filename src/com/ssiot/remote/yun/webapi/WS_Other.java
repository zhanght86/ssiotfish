package com.ssiot.remote.yun.webapi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.ssiot.remote.Utils;
import com.ssiot.remote.data.DbHelperSQL;
import com.ssiot.remote.data.DbHelperSQLFish;
import com.ssiot.remote.data.SsiotResult;
import com.ssiot.remote.data.model.AnswerModel;
import com.ssiot.remote.data.model.ArticleModel;
import com.ssiot.remote.data.model.ArticleTypeModel;
import com.ssiot.remote.data.model.DiseaseModel;
import com.ssiot.remote.data.model.FishPartModel;
import com.ssiot.remote.data.model.FishTypeModel;
import com.ssiot.remote.data.model.GoodsModel;
import com.ssiot.remote.data.model.ManualAnalysisModel;
import com.ssiot.remote.data.model.ManualDataModel;
import com.ssiot.remote.data.model.QuestionModel;
import com.ssiot.remote.data.model.SensorModel;
import com.ssiot.remote.data.model.SymptomModel;
import com.ssiot.remote.data.model.WaterColorDiagnoseModel;
import com.ssiot.remote.data.model.WaterDetailModel;
import com.ssiot.remote.yun.monitor.YunNodeModel;

public class WS_Other extends WebBasedb2{
	private static final String tag = "WS_Other";
	
	private String MethodFile = "SensorProject.asmx";
	
	public List<FishTypeModel> GetAllFishTypes(String account){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("account", "" + account);
        String txt = exeRetString(MethodFile, "GetAllFishTypes", params);
        return null;
	}
	
	public List<ArticleTypeModel> GetAllFishArticleTypes (){
		String cmd = "select * from iot2014.dbo.biz_ArticleType where UserId=226";
		SsiotResult sr = DbHelperSQL.getInstance().Query(cmd);
		List<ArticleTypeModel> list = new ArrayList<ArticleTypeModel>();
		if (null != sr && sr.mRs != null){
			ResultSet rs = sr.mRs;
			try {
				while(rs.next()){
					ArticleTypeModel m = new ArticleTypeModel();
					m._id = rs.getInt("ID");
					m._parentcode = rs.getString("ParentCode");
					m._code = rs.getString("Code");
					m._name = rs.getString("Name");
					list.add(m);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (null != sr){
			sr.close();
		}
		return list;
	}
	
	public List<ArticleModel> GetArticles(int pageindex, int pagesize, String where){
		String cmd = "select * from iot2014.dbo.biz_Article where UserId=226 and " + where;
		SsiotResult sr = DbHelperSQL.getInstance().Query(cmd);
		List<ArticleModel> list = new ArrayList<ArticleModel>();
		if (null != sr && sr.mRs != null){
			ResultSet rs = sr.mRs;
			try {
				while(rs.next()){
					ArticleModel m = new ArticleModel();
					m._id = rs.getInt("ID");
					m._type = rs.getString("ArticleTypeCode");
					m._title = rs.getString("Title");
					m._description = rs.getString("Description");
					m._image = rs.getString("Image");
					m._createtime = rs.getTimestamp("CreateTime");
					m._author = rs.getString("Author");
					list.add(m);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (null != sr){
			sr.close();
		}
		return list;
	}
	
	public String GetArticleContent(int articleid){
		String cmd = "select * from iot2014.dbo.biz_ArticleContent where ArticleId=" + articleid;
		SsiotResult sr = DbHelperSQL.getInstance().Query(cmd);
//		List<ArticleModel> list = new ArrayList<ArticleModel>();
		String contentStr = "";
		if (null != sr && sr.mRs != null){
			ResultSet rs = sr.mRs;
			try {
				if(rs.next()){
					contentStr = rs.getString("ContentText");
//					ArticleModel m = new ArticleModel();
//					m._id = rs.getInt("ID");
//					m._type = rs.getString("ArticleTypeCode");
//					m._title = rs.getString("Title");
//					m._description = rs.getString("Description");
//					m._image = rs.getString("Image");
//					m._createtime = rs.getTimestamp("CreateTime");
//					m._author = rs.getString("Author");
//					list.add(m);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (null != sr){
			sr.close();
		}
		return contentStr;
	}
	
}