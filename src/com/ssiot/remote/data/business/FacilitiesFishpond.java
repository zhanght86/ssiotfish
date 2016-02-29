package com.ssiot.remote.data.business;

import com.ssiot.remote.data.DbHelperSQL;
import com.ssiot.remote.data.SsiotResult;
import com.ssiot.remote.data.model.FacilitiesFishpondModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FacilitiesFishpond{
    public int Add(FacilitiesFishpondModel model){
        StringBuilder strSql = new StringBuilder();
        strSql.append("insert into iot_FacilitiesFishpond(");
        strSql.append("FacilitiesAddress,FacilitiesSize,FacilitiesName,ParentID,FPID,FChildNum,Longitude,Latitude)");
        strSql.append(" values (");
        strSql.append("?,?,?,?,?,?,?,?) ");
        strSql.append(";select @@IDENTITY");
        
        ArrayList<Object> parameters = new ArrayList<Object>();
        parameters.add(model._addr);
        parameters.add(model._size);
        parameters.add(model._name);
        parameters.add(model._parentid);
        parameters.add(model._fpid);
        parameters.add(model._childNum);
        parameters.add(model._longitude);
        parameters.add(model._latitute);
        return DbHelperSQL.getInstance().Update_object(strSql.toString(), parameters);
    }
    
    public List<FacilitiesFishpondModel> GetModelList(String strWhere) {
        StringBuilder strSql = new StringBuilder();
        strSql.append("select * ");
        strSql.append(" FROM iot_FacilitiesFishpond ");
        if (strWhere.trim() != "") {
            strSql.append(" where " + strWhere);
        }
        SsiotResult sResult = DbHelperSQL.getInstance().Query(strSql.toString());
        List<FacilitiesFishpondModel> list = null;
        if (null != sResult && sResult.mRs != null){
            list = DataTableToList(sResult.mRs);
        }
        if (null != sResult){
            sResult.close();
        }
        return list;
    }
    
    public List<FacilitiesFishpondModel> DataTableToList(ResultSet c){
        List<FacilitiesFishpondModel> models = new ArrayList<FacilitiesFishpondModel>();
        FacilitiesFishpondModel m = new FacilitiesFishpondModel();
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
    
    private FacilitiesFishpondModel DataRowToModel(ResultSet c){
        FacilitiesFishpondModel m = new FacilitiesFishpondModel();
        try {
            m._id = c.getInt("FacilitiesID");
            m._addr = c.getString("FacilitiesAddress");
            m._size = c.getFloat("FacilitiesSize");
            m._name = c.getString("FacilitiesName");
            m._parentid = c.getInt("ParentID");
            m._fpid = c.getInt("FPID");
            m._childNum = c.getInt("FChildNum");
            m._longitude = c.getFloat("Longitude");
            m._latitute = c.getFloat("Latitude");
            return m;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } 
}