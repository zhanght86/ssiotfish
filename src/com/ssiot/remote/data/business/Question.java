package com.ssiot.remote.data.business;

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
        strSql.append("ID,UserID,Title,ContentText,CreateTime,PicUrls,Addr,LongiTude,Latitude,ReplyCount)");
        strSql.append(" values (");
        strSql.append("?,?,?,?,?,?,?,?,?,?) ");
        strSql.append(";select @@IDENTITY");
        
        ArrayList<Object> parameters = new ArrayList<Object>();
        parameters.add(model._id);
        parameters.add(model._userId);
        parameters.add(model._title);
        parameters.add(model._contentText);
        parameters.add(model._createTime);
        parameters.add(model._picUrls);
        parameters.add(model._addr);
        parameters.add(model._longitude);
        parameters.add(model._latitude);
        parameters.add(model._replyCount);
        return DbHelperSQL.getInstance().Update_object(strSql.toString(), parameters);
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
            m._picUrls = c.getString("PicUrils");
            m._addr = c.getString("Addr");
            m._longitude = c.getFloat("LongiTude");
            m._latitude = c.getFloat("LatiTude");
            m._replyCount = c.getInt("ReplyCount");
            return m;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } 
}