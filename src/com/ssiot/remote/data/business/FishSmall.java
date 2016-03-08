package com.ssiot.remote.data.business;

import com.ssiot.remote.data.DbHelperSQL;
import com.ssiot.remote.data.SsiotResult;
import com.ssiot.remote.data.model.FishDrugModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FishSmall{
    public int Add(FishDrugModel model){//TODO 由于数据结构一样，临时的FishDrugModel = FishFeedModel
        StringBuilder strSql = new StringBuilder();
        strSql.append("insert into cms2016.dbo.FishSmall(");
        strSql.append("UserID,Name,Amount,Area,Worker,ActionTime,DetailText)");
        strSql.append(" values (");
        strSql.append("?,?,?,?,?,?,?) ");
        strSql.append(";select @@IDENTITY");
        
        ArrayList<Object> parameters = new ArrayList<Object>();
        parameters.add(model._userid);
        parameters.add(model._name);
        parameters.add(model._amount);
        parameters.add(model._area);
        parameters.add(model._worker);
        parameters.add(model._actiontime);
        parameters.add(model._detailtext);
        return DbHelperSQL.getInstance().Update_object(strSql.toString(), parameters);
    }
    
    public List<FishDrugModel> GetModelList(String strWhere) {
        StringBuilder strSql = new StringBuilder();
        strSql.append("select * ");
        strSql.append(" FROM cms2016.dbo.FishSmall ");
        if (strWhere.trim() != "") {
            strSql.append(" where " + strWhere);
        }
        SsiotResult sResult = DbHelperSQL.getInstance().Query(strSql.toString());
        List<FishDrugModel> list = null;
        if (null != sResult && sResult.mRs != null){
            list = DataTableToList(sResult.mRs);
        }
        if (null != sResult){
            sResult.close();
        }
        return list;
    }
    
    public List<FishDrugModel> DataTableToList(ResultSet c){
        List<FishDrugModel> models = new ArrayList<FishDrugModel>();
        FishDrugModel m = new FishDrugModel();
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
    
    private FishDrugModel DataRowToModel(ResultSet c){
        FishDrugModel m = new FishDrugModel();
        try {
            m._id = c.getInt("ID");
            m._userid = c.getInt("UserID");
            m._name = c.getString("Name");
            m._amount = c.getString("Amount");
            m._area = c.getString("Area");
            m._worker = c.getString("Worker");
            m._actiontime = c.getTimestamp("ActionTime");
            m._detailtext = c.getString("DetailText");
            return m;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } 
}