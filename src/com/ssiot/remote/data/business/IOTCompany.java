package com.ssiot.remote.data.business;

import com.ssiot.remote.data.DbHelperSQL;
import com.ssiot.remote.data.SsiotResult;
import com.ssiot.remote.data.model.IOTCompanyModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IOTCompany{
//    public int Add(IOTCompanyModel model){
//        StringBuilder strSql = new StringBuilder();
//        strSql.append("insert into iot_IOTCompany(");
//        strSql.append("FacilitiesAddress,FacilitiesSize,FacilitiesName,ParentID,FPID,FChildNum,Longitude,Latitude)");
//        strSql.append(" values (");
//        strSql.append("?,?,?,?,?,?,?,?) ");
//        strSql.append(";select @@IDENTITY");
//        
//        ArrayList<Object> parameters = new ArrayList<Object>();
//        parameters.add(model._addr);
//        parameters.add(model._size);
//        parameters.add(model._name);
//        parameters.add(model._parentid);
//        parameters.add(model._fpid);
//        parameters.add(model._childNum);
//        parameters.add(model._longitude);
//        parameters.add(model._latitute);
//        return DbHelperSQL.getInstance().Update_object(strSql.toString(), parameters);
//    }
    
    public List<IOTCompanyModel> GetModelList(String strWhere) {
        StringBuilder strSql = new StringBuilder();
        strSql.append("select * ");
        strSql.append(" FROM iot_Company ");
        if (strWhere.trim() != "") {
            strSql.append(" where " + strWhere);
        }
        SsiotResult sResult = DbHelperSQL.getInstance().Query(strSql.toString());
        List<IOTCompanyModel> list = null;
        if (null != sResult && sResult.mRs != null){
            list = DataTableToList(sResult.mRs);
        }
        if (null != sResult){
            sResult.close();
        }
        return list;
    }
    
    public List<IOTCompanyModel> DataTableToList(ResultSet c){
        List<IOTCompanyModel> models = new ArrayList<IOTCompanyModel>();
        IOTCompanyModel m = new IOTCompanyModel();
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
    
    private IOTCompanyModel DataRowToModel(ResultSet c){
        IOTCompanyModel m = new IOTCompanyModel();
        try {
            m._companyid = c.getInt("CompanyID");
            m._name = c.getString("Name");
            m._url = c.getString("Url");
            m._parentid = c.getInt("ParentID");
            m._createTime = c.getTimestamp("CreateTime");
            m._modifyTime = c.getTimestamp("ModifyTime");
            m._isdelete = c.getBoolean("isDelete");
            m._companyContent = c.getString("CompanyContent");
            m._remark = c.getString("Remark");
            return m;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } 
}