package com.ssiot.remote.data.business;

import com.ssiot.remote.data.DbHelperSQL;
import com.ssiot.remote.data.SsiotResult;
import com.ssiot.remote.data.model.UserGroupModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserGroup{
    public int Add(UserGroupModel model){
        StringBuilder strSql = new StringBuilder();
        strSql.append("insert into iot2014.dbo.iot_UserGroup(");
        strSql.append("UserGroupID,UserGroupName,Parentid,IsDelete)");
        strSql.append(" values (");
        strSql.append("?,?,?,?) ");
        strSql.append(";select @@IDENTITY");
        
        ArrayList<Object> parameters = new ArrayList<Object>();
        parameters.add(model._id);
        parameters.add(model._groupname);
        parameters.add(model._parentid);
        parameters.add(model._isdelete);
        return DbHelperSQL.getInstance().Update_object(strSql.toString(), parameters);
    }
    
    public List<UserGroupModel> GetModelList(String strWhere) {
        StringBuilder strSql = new StringBuilder();
        strSql.append("select * ");
        strSql.append(" FROM iot2014.dbo.iot_UserGroup ");
        if (strWhere.trim() != "") {
            strSql.append(" where " + strWhere);
        }
        SsiotResult sResult = DbHelperSQL.getInstance().Query(strSql.toString());
        List<UserGroupModel> list = null;
        if (null != sResult && sResult.mRs != null){
            list = DataTableToList(sResult.mRs);
        }
        if (null != sResult){
            sResult.close();
        }
        return list;
    }
    
    public List<UserGroupModel> DataTableToList(ResultSet c){
        List<UserGroupModel> models = new ArrayList<UserGroupModel>();
        UserGroupModel m = new UserGroupModel();
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
    
    private UserGroupModel DataRowToModel(ResultSet c){
        UserGroupModel m = new UserGroupModel();
        try {
            m._id = c.getInt("UserGroupID");
            m._groupname = c.getString("UserGroupName");
            m._parentid = c.getInt("Parentid");
            m._isdelete = c.getBoolean("IsDelete");
            return m;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } 
}