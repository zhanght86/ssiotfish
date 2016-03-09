package com.ssiot.remote.data.business;

import com.ssiot.remote.data.DbHelperSQL;
import com.ssiot.remote.data.SsiotResult;
import com.ssiot.remote.data.model.TaskReportModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskReport{
    public int Add(TaskReportModel model){
        StringBuilder strSql = new StringBuilder();
        strSql.append("insert into cms2016.dbo.TaskReport(");
        strSql.append("TaskID,CreateTime,Feed,Drug,SmallObject,Img,Location,ContentText,Finish,ReplyText,UserID)");
        strSql.append(" values (");
        strSql.append("?,?,?,?,?,?,?,?,?,?,?) ");
        strSql.append(";select @@IDENTITY");
        
        ArrayList<Object> parameters = new ArrayList<Object>();
        parameters.add(model._taskid);
        parameters.add(model._createtime);
        parameters.add(model._feedStr);
        parameters.add(model._drugStr);
        parameters.add(model._smallfishStr);
        parameters.add(model._img);
        parameters.add(model._location);
        parameters.add(model._contenttext);
        parameters.add(model._isfinish);
        parameters.add(model._replytext);
        parameters.add(model._userid);
        return DbHelperSQL.getInstance().Update_object(strSql.toString(), parameters);
    }
    
    public List<TaskReportModel> GetModelList(String strWhere) {
        StringBuilder strSql = new StringBuilder();
        strSql.append("select * ");
        strSql.append(" FROM cms2016.dbo.TaskReport ");
        if (strWhere.trim() != "") {
            strSql.append(" where " + strWhere + " order by CreateTime desc");
        }
        SsiotResult sResult = DbHelperSQL.getInstance().Query(strSql.toString());
        List<TaskReportModel> list = null;
        if (null != sResult && sResult.mRs != null){
            list = DataTableToList(sResult.mRs);
        }
        if (null != sResult){
            sResult.close();
        }
        return list;
    }
    
    public List<TaskReportModel> GetModelViewList(String wherestr) {//把用户名从USer表里查出来了
        StringBuilder strSql = new StringBuilder();
        strSql.append("select iot_user.UserName,t.* from " +
                "(select * from cms2016.dbo.TaskReport where "+ wherestr+") as t inner join iot2014.dbo.iot_user on t.UserID=iot_User.UserID " +
                		"order by CreateTime desc");
        SsiotResult sResult = DbHelperSQL.getInstance().Query(strSql.toString());
        List<TaskReportModel> list = null;
        if (null != sResult && sResult.mRs != null){
            list = DataTableToList(sResult.mRs);
        }
        if (null != sResult){
            sResult.close();
        }
        return list;
    }
    
    public List<TaskReportModel> DataTableToList(ResultSet c){
        List<TaskReportModel> models = new ArrayList<TaskReportModel>();
        TaskReportModel m = new TaskReportModel();
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
    
    private TaskReportModel DataRowToModel(ResultSet c){
        TaskReportModel m = new TaskReportModel();
        try {
            m._id = c.getInt("ID");
            m._taskid = c.getInt("TaskID");
            m._createtime = c.getTimestamp("CreateTime");
            m._feedStr = c.getString("Feed");
            m._drugStr = c.getString("Drug");
            m._smallfishStr = c.getString("SmallObject");
            m._img = c.getString("Img");
            m._location = c.getString("Location");
            m._contenttext = c.getString("ContentText");
            m._isfinish = c.getBoolean("Finish");
            m._replytext = c.getString("ReplyText");
            m._userid = c.getInt("UserID");
            try {
                m._username = c.getString("UserName");
            } catch (Exception e) {
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