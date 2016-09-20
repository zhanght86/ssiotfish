package com.ssiot.remote.data.business;

import com.ssiot.remote.data.DbHelperSQL;
import com.ssiot.remote.data.SsiotResult;
import com.ssiot.remote.data.model.AgricultureFacilityModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AgricultureFacility{
//    public int Add(AgricultureFacilityModel model){
//        StringBuilder strSql = new StringBuilder();
//        strSql.append("insert into iot2014.dbo.iot_AgricultureFacilities(");
//        strSql.append("NodeUniqueID,Relation,RuleStr)");
//        strSql.append(" values (");
//        strSql.append("?,?,?) ");
//        strSql.append(";select @@IDENTITY");
//        
//        ArrayList<Object> parameters = new ArrayList<Object>();
//        parameters.add(model._uniqueID);
//        parameters.add(model._relation);
//        parameters.add(model._ruleStr);
//        return DbHelperSQL.getInstance().Update_object(strSql.toString(), parameters);
//    }
    
//    public boolean Exists(String uniqueID){
//        StringBuilder strSql = new StringBuilder();
//        strSql.append("select * from iot2014.dbo.iot_AgricultureFacilities where NodeUniqueID=?");
//        ArrayList<Object> parameters = new ArrayList<Object>();
//        parameters.add(uniqueID);
//        return DbHelperSQL.getInstance().Exists_a(strSql.toString(), parameters);
//    }
//    
//    public boolean Update(AgricultureFacilityModel model){//注意 是以NodeUniqueID 为查询的
//        StringBuilder strSql = new StringBuilder();
//        strSql.append("update iot2014.dbo.iot_AgricultureFacilities set ");
////        strSql.append("NodeUniqueID=?,");
//        strSql.append("Relation=?,");
//        strSql.append("RuleStr=?,");
//        strSql.append(" NodeUniqueID =?");
//        
//        ArrayList<Object> parameters = new ArrayList<Object>();
////        parameters.add(model._uniqueID);
//        parameters.add(model._relation);
//        parameters.add(model._ruleStr);
//        parameters.add(model._uniqueID);
//        
//        int rows = DbHelperSQL.getInstance().Update_object(strSql.toString(), parameters);
//        return rows > 0;
//    }
    
//    public boolean Delete(String nodeunique){
//        String cmd = "delete from iot2014.dbo.iot_AgricultureFacilities where NodeUniqueID='" + nodeunique + "'";
//        return DbHelperSQL.getInstance().Update(cmd) > 0;
//    }
    
    public List<AgricultureFacilityModel> GetModelList(String strWhere) {
        StringBuilder strSql = new StringBuilder();
        strSql.append("select * ");
        strSql.append(" FROM iot2014.dbo.iot_AgricultureFacilities ");
        if (strWhere.trim() != "") {
            strSql.append(" where " + strWhere);
        }
        SsiotResult sResult = DbHelperSQL.getInstance().Query(strSql.toString());
        List<AgricultureFacilityModel> list = null;
        if (null != sResult && null != sResult.mRs){
            list = DataTableToList(sResult.mRs);
        }
        if (null != sResult){
            sResult.close();
        }
        return list;
    }
    
    public List<AgricultureFacilityModel> DataTableToList(ResultSet c){
        List<AgricultureFacilityModel> models = new ArrayList<AgricultureFacilityModel>();
        AgricultureFacilityModel m = new AgricultureFacilityModel();
        try {
            while(c.next()){
                m = DataRowToModel(c);
                if (m != null){
                    models.add(m);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return models;
    }
    
    private AgricultureFacilityModel DataRowToModel(ResultSet c){
        AgricultureFacilityModel m = new AgricultureFacilityModel();
        try {
            m._id = c.getInt("FacilitiesID");
            m._name = c.getString("FacilitiesName");
            m._landid = c.getInt("LandID");
            m._areaid = c.getInt("AreaID");
            return m;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } 
}
