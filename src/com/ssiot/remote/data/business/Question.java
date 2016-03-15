package com.ssiot.remote.data.business;

import android.text.TextUtils;

import com.ssiot.remote.data.DbHelperSQL;
import com.ssiot.remote.data.SsiotResult;
import com.ssiot.remote.data.model.QuestionModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Question{
    //iot_TraceImages
    
    public int Add(QuestionModel model){
        StringBuilder strSql = new StringBuilder();
        strSql.append("insert into Question(");
        strSql.append("UserID,Title,ContentText,CreateTime,PicUrls,Addr,LongiTude,Latitude,ReplyCount,QuestionType)");
        strSql.append(" values (");
        strSql.append("?,?,?,?,?,?,?,?,?,?) ");
        strSql.append(";select @@IDENTITY");
        
        ArrayList<Object> parameters = new ArrayList<Object>();
        parameters.add(model._userId);
        parameters.add(model._title);
        parameters.add(model._contentText);
        parameters.add(model._createTime);
        parameters.add(model._picUrls);
        parameters.add(model._addr);
        parameters.add(model._longitude);
        parameters.add(model._latitude);
        parameters.add(model._replyCount);
        parameters.add(model._type);
        return DbHelperSQL.getInstance().Update_object(strSql.toString(), parameters);
    }
    
    public int UpdateReplyCount(int questionid, int replaycount){
        StringBuilder strSql = new StringBuilder();
        strSql.append("update Question set ReplyCount=" + replaycount + " where ID=" + questionid);
        
        return DbHelperSQL.getInstance().Update(strSql.toString());
    }
    
    public List<QuestionModel> GetPageList(int pageIndex){
//        select * from (select *,Row_number() over(order by vlcVideoInfoID desc ) as row  from VLCVideoInfo)T where T.row between 1 and 3
//        String sql = "select *,Row_number() over(order by vlcVideoInfoID desc ) as row  from VLCVideoInfo";
        String sql = "select * from (select ROW_NUMBER() OVER (order by T.ID desc) AS ROW,T.* FROM Question T)TT " +
        		"WHERE TT.ROW between " + (pageIndex*10 + 1) + " and " + (pageIndex*10 + 10);
        SsiotResult sResult = DbHelperSQL.getInstance().Query(sql);
        List<QuestionModel> list = null;
        if (null != sResult && sResult.mRs != null){
            list = DataTableToList(sResult.mRs);
        }
        if (null != sResult){
            sResult.close();
        }
        return list;
    }
    
    public List<QuestionModel> GetPageViewList(int pageIndex,String strWh){//与iot_User表联合查询
        StringBuilder builder = new StringBuilder();
        builder.append("select iot_user.UserName,questmp.* from ");
        String whereStr = "";
        if (!TextUtils.isEmpty(strWh)){
            whereStr = " where " + strWh + " "; 
        }
        String sql = "select * from (select ROW_NUMBER() OVER (order by T.ID desc) AS ROW,T.* FROM Question T "+whereStr+")TT "+
                "WHERE TT.ROW between " + (pageIndex * 10 + 1) + " and " + (pageIndex * 10 + 10);
        builder.append("(" + sql + ") as questmp ");
        builder.append("left join iot2014.dbo.iot_user on questmp.UserID=iot_User.UserID");//left join 以左表为准
        SsiotResult sResult = DbHelperSQL.getInstance().Query(builder.toString());
        List<QuestionModel> list = null;
        if (null != sResult && sResult.mRs != null) {
            list = DataTableToList(sResult.mRs);
        }
        if (null != sResult) {
            sResult.close();
        }
        return list;
  }
    
    public List<QuestionModel> GetModelList(String strWhere) {
        StringBuilder strSql = new StringBuilder();
        strSql.append("select * ");
        strSql.append(" FROM Question ");
        if (strWhere.trim() != "") {
            strSql.append(" where " + strWhere);
        }
        SsiotResult sResult = DbHelperSQL.getInstance().Query(strSql.toString());
        List<QuestionModel> list = null;
        if (null != sResult && sResult.mRs != null){
            list = DataTableToList(sResult.mRs);
        }
        if (null != sResult){
            sResult.close();
        }
        return list;
    }
    
    public List<QuestionModel> DataTableToList(ResultSet c){
        List<QuestionModel> models = new ArrayList<QuestionModel>();
        QuestionModel m = new QuestionModel();
        try {
            while(c.next()){
                m = DataRowToModel(c);
                if (m != null){
                    models.add(m);
                }
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return models;
    }
    
    private QuestionModel DataRowToModel(ResultSet c){
        QuestionModel m = new QuestionModel();
        try {
            m._id = c.getInt("ID");
            m._userId = c.getInt("UserID");
            m._title = c.getString("Title");
            m._contentText = c.getString("ContentText");
            m._createTime = c.getTimestamp("CreateTime");
            m._picUrls = c.getString("PicUrls");
            m._addr = c.getString("Addr");
            m._longitude = c.getFloat("LongiTude");
            m._latitude = c.getFloat("LatiTude");
            m._replyCount = c.getInt("ReplyCount");
            m._type = c.getInt("QuestionType");
            try {
                m._username = c.getString("UserName");
            } catch (Exception e) {
//                c.findColumn(columnName)
                e.printStackTrace();
            }
            return m;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } 
}