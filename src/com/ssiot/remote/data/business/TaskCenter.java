package com.ssiot.remote.data.business;

import com.ssiot.remote.data.DbHelperSQL;
import com.ssiot.remote.data.SsiotResult;
import com.ssiot.remote.data.model.TaskCenterModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskCenter{
    public int Add(TaskCenterModel model){
        StringBuilder strSql = new StringBuilder();
        strSql.append("insert into cms2016.dbo.TaskCenter(");
        strSql.append("UserID,ToUsersID,ContentText,CreateTime,StartTime,EndTime,Img,NeedImg,NeedLocation,State)");
        strSql.append(" values (");
        strSql.append("?,?,?,?,?,?,?,?,?,?) ");
        strSql.append(";select @@IDENTITY");
        
        ArrayList<Object> parameters = new ArrayList<Object>();
        parameters.add(model._userid);
        parameters.add(model._tousers);
        parameters.add(model._contenttext);
        parameters.add(model._createtime);
        parameters.add(model._starttime);
        parameters.add(model._endtime);
        parameters.add(model._img);
        parameters.add(model._needimg);
        parameters.add(model._needlocation);
        parameters.add(model._state);
        return DbHelperSQL.getInstance().Update_object(strSql.toString(), parameters);
    }
    
    public boolean UpdateState(int id, int state){
        String cmd = "update cms2016.dbo.TaskCenter set State=" + state + " where ID=" + id;
        return DbHelperSQL.getInstance().Update(cmd) > 0;
    }
    
    public List<TaskCenterModel> GetModelList(String strWhere) {
        StringBuilder strSql = new StringBuilder();
        strSql.append("select * ");
        strSql.append(" FROM cms2016.dbo.TaskCenter ");
        if (strWhere.trim() != "") {
            strSql.append(" where " + strWhere);
        }
        SsiotResult sResult = DbHelperSQL.getInstance().Query(strSql.toString());
        List<TaskCenterModel> list = null;
        if (null != sResult && sResult.mRs != null){
            list = DataTableToList(sResult.mRs);
        }
        if (null != sResult){
            sResult.close();
        }
        return list;
    }
    
    public List<TaskCenterModel> DataTableToList(ResultSet c){
        List<TaskCenterModel> models = new ArrayList<TaskCenterModel>();
        TaskCenterModel m = new TaskCenterModel();
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
    
    private TaskCenterModel DataRowToModel(ResultSet c){
        TaskCenterModel m = new TaskCenterModel();
        try {
            m._id = c.getInt("ID");
            m._userid = c.getInt("UserID");
            m._tousers = c.getString("ToUsersID");
            m._contenttext = c.getString("ContentText");
            m._createtime = c.getTimestamp("CreateTime");
            m._starttime = c.getTimestamp("StartTime");
            m._endtime = c.getTimestamp("EndTime");
            m._img = c.getString("Img");
            m._needimg = c.getBoolean("NeedImg");
            m._needlocation = c.getBoolean("NeedLocation");
            m._state = c.getInt("State");
            return m;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } 
}