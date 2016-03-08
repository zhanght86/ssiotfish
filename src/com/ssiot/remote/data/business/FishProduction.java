package com.ssiot.remote.data.business;

import com.ssiot.remote.data.DbHelperSQL;
import com.ssiot.remote.data.SsiotResult;
import com.ssiot.remote.data.model.FishProductionModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FishProduction{
    public int Add(FishProductionModel model){
        StringBuilder strSql = new StringBuilder();
        strSql.append("insert into cms2016.dbo.FishProduction(");
        strSql.append("UserID,ProductionType,Name,Amount,TotalPrice,Detail,Worker,IsProductIn,ActionTime)");
        strSql.append(" values (");
        strSql.append("?,?,?,?,?,?,?,?,?) ");
        strSql.append(";select @@IDENTITY");
        
        ArrayList<Object> parameters = new ArrayList<Object>();
        parameters.add(model._userid);
        parameters.add(model._productiontype);
        parameters.add(model._name);
        parameters.add(model._amount);
        parameters.add(model._totalprice);
        parameters.add(model._detail);
        parameters.add(model._worker);
        parameters.add(model._isProductIn);
        parameters.add(model._actiontime);
        return DbHelperSQL.getInstance().Update_object(strSql.toString(), parameters);
    }
    
    public List<FishProductionModel> GetModelList(String strWhere) {
        StringBuilder strSql = new StringBuilder();
        strSql.append("select * ");
        strSql.append(" FROM cms2016.dbo.FishProduction ");
        if (strWhere.trim() != "") {
            strSql.append(" where " + strWhere);
        }
        SsiotResult sResult = DbHelperSQL.getInstance().Query(strSql.toString());
        List<FishProductionModel> list = null;
        if (null != sResult && sResult.mRs != null){
            list = DataTableToList(sResult.mRs);
        }
        if (null != sResult){
            sResult.close();
        }
        return list;
    }
    
    public List<FishProductionModel> DataTableToList(ResultSet c){
        List<FishProductionModel> models = new ArrayList<FishProductionModel>();
        FishProductionModel m = new FishProductionModel();
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
    
    private FishProductionModel DataRowToModel(ResultSet c){
        FishProductionModel m = new FishProductionModel();
        try {
            m._id = c.getInt("ID");
            m._userid = c.getInt("UserID");
            m._productiontype = c.getInt("ProductionType");
            m._name = c.getString("Name");
            m._amount = c.getString("Amount");
            m._totalprice = c.getFloat("TotalPrice");
            m._detail = c.getString("Detail");
            m._worker = c.getString("Worker");
            m._isProductIn = c.getBoolean("IsProductIn");
            m._actiontime = c.getTimestamp("ActionTime");
            return m;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } 
}