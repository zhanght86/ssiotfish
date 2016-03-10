package com.ssiot.remote.data.business;

import com.ssiot.remote.data.DbHelperSQL;
import com.ssiot.remote.data.SsiotResult;
import com.ssiot.remote.data.model.AnswerModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Answer{
    public int Add(AnswerModel model){
        StringBuilder strSql = new StringBuilder();
        strSql.append("insert into Answer(");
        strSql.append("QuestionID,UserID,UserName,Addr,UserPicUrl,CreateTime,ContentText)");
        strSql.append(" values (");
        strSql.append("?,?,?,?,?,?,?) ");
        strSql.append(";select @@IDENTITY");
        
        ArrayList<Object> parameters = new ArrayList<Object>();
        parameters.add(model._questionid);
        parameters.add(model._userid);
        parameters.add(model._username);
        parameters.add(model._addr);
        parameters.add(model._userpicurl);
        parameters.add(model._createTime);
        parameters.add(model._contenttext);
        return DbHelperSQL.getInstance().Update_object(strSql.toString(), parameters);
    }
    
    public List<AnswerModel> GetModelList(String strWhere) {
        StringBuilder strSql = new StringBuilder();
        strSql.append("select * ");
        strSql.append(" FROM Answer ");
        if (strWhere.trim() != "") {
            strSql.append(" where " + strWhere);
        }
        SsiotResult sResult = DbHelperSQL.getInstance().Query(strSql.toString());
        List<AnswerModel> list = null;
        if (null != sResult && sResult.mRs != null){
            list = DataTableToList(sResult.mRs);
        }
        if (null != sResult){
            sResult.close();
        }
        return list;
    }
    
//    public List<AnswerModel> GetModelViewList(String strWhere) {//把用户的地址信息也联合查询出来
//        StringBuilder strSql = new StringBuilder();
//        strSql.append("select iot_user.Address,t.* from " +
//                "(select * from Answer where "+ strWhere+") as t inner join iot2014.dbo.iot_user on t.UserID=iot_User.UserID");
//        SsiotResult sResult = DbHelperSQL.getInstance().Query(strSql.toString());
//        List<AnswerModel> list = null;
//        if (null != sResult && sResult.mRs != null){
//            list = DataTableToList(sResult.mRs);
//        }
//        if (null != sResult){
//            sResult.close();
//        }
//        return list;
//    }
    
    public List<AnswerModel> DataTableToList(ResultSet c){
        List<AnswerModel> models = new ArrayList<AnswerModel>();
        AnswerModel m = new AnswerModel();
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
    
    private AnswerModel DataRowToModel(ResultSet c){
        AnswerModel m = new AnswerModel();
        try {
            m._id = c.getInt("ID");
            m._questionid = c.getInt("QuestionID");
            m._userid = c.getInt("UserID");
            m._username = c.getString("UserName");
            m._addr = c.getString("Addr");
            m._userpicurl = c.getString("UserPicUrl");
            m._createTime = c.getTimestamp("CreateTime");
            m._contenttext = c.getString("ContentText");
            return m;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } 
}