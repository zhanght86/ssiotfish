package com.ssiot.remote.data.business;

import android.text.TextUtils;
import android.util.Log;

import com.ssiot.remote.data.DbHelperSQL;
import com.ssiot.remote.data.SsiotResult;
import com.ssiot.remote.data.model.LatestDataModel;
import com.ssiot.remote.data.model.view.SensorViewModel;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LatestData{
    private static final String tag = "LatestData";
    
    private static Sensor mSensor = new Sensor();
    
    public LatestData(){
        
    }
    
    public List<LatestDataModel> GetModelList(String strWhere){
        List<LatestDataModel> list = null;
        SsiotResult sResult = GetList(strWhere);
        if (null != sResult && null != sResult.mRs){
            list = DataTableToList(sResult.mRs);
        }
        if (null != sResult){
            sResult.close();
        }
        return list;
    }
    
    public SsiotResult GetLastData(){
        return GetLastData("节点编号", -1, -1, true, "");
    }
    
    public SsiotResult GetLastData(String nodelist){
        return GetLastData("节点编号", -1, -1, true, nodelist);
    }
    
    public SsiotResult GetLastData(String nodelist, int startNum, int endNum){
        return GetLastData("节点编号", startNum, endNum, true, nodelist);
    }
    
    public SsiotResult GetLastData(String orderby ,String nodelist, int startNum, int endNum){
        return GetLastData(orderby, startNum, endNum, true, nodelist);
    }
    
    public SsiotResult GetLastData(String orderby, int beginindex, int endindex, boolean unit, String nodelist) {
//        String key = orderby + beginindex + endindex + unit + nodelist;
//        String hashkey = key.GetHashCode().ToString();//获取hashkey为字典ID
//        // string CacheKey = "LatestData-GetLastData-" + hashkey.ToString();
//        String CacheKey = Guid.NewGuid().ToString();
//        object objModel = Angel.Common.Web.DataCache.GetCache(CacheKey);
        SsiotResult objModel = null;
        if (objModel == null) {
            try {
                objModel = GetLastData_data(orderby, beginindex, endindex, unit, nodelist);
                if (objModel != null) {
                    // int ModelCache =
                    // Angel.Common.Web.ConfigHelper.GetConfigInt("ModelCache");
                    // Angel.Common.Web.DataCache.SetCache(CacheKey, objModel,
                    // DateTime.Now.AddMinutes(2), TimeSpan.Zero);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (SsiotResult) objModel;
    }
    
//    public GetTwoNodeView2ListBynode_list_GetLastData(){
//        
//    }
    
 // 获取最新数据列表
    private SsiotResult GetLastData_data(String orderby, int beginindex, int endindex, boolean unit,String nodelist){
        SsiotResult rs = null;
        String strwhere = "";
        if (TextUtils.isEmpty(nodelist)){
//            strwhere = " where 1=1";//bug
            return null;
        } else {
            strwhere = "WHERE UniqueID IN (SELECT UniqueID FROM Node WHERE NodeNo IN("+nodelist+"))";
        }
        List<SensorViewModel> list = new ArrayList<SensorViewModel>();
        list = mSensor.GetSensorListByNodeNoString(nodelist);
        
        if (null != list && list.size() > 0){
            StringBuilder sb_sql = new StringBuilder();
            sb_sql.append("SELECT NodeNo AS 节点编号 , Location AS 安装地点 ,Longitude AS 经度,[Latitude] AS 纬度, [DATETIME] AS 更新时间 ,ishavevideo as 是否有摄像头,");
            StringBuilder sb_columns1 = new StringBuilder();
            StringBuilder sb_columns2 = new StringBuilder();
            for (int i = 0; i < list.size(); i++){
                String unitstr = "";
                String sensoradd = "";
                if (unit){
                    unitstr = " + '" + list.get(i)._unit + "'";
                }
                if (list.get(i)._channel > 0){
                    sensoradd = "" + list.get(i)._channel;
                }
                sb_columns1.append(" replace(convert(VARCHAR,convert(float,round(SensorColumn.[" +
                		list.get(i)._sensorno + "_" + list.get(i)._channel + "]," + list.get(i)._accuracy + ")))" +
                		unitstr + ", '32767'" + unitstr +", '无') AS ["+ list.get(i)._shortname +sensoradd +"] ,");
                sb_columns2.append("[" + list.get(i)._sensorno + "_"+list.get(i)._channel+"],");
            }
//            Log.v(tag, "^^^^^^^^^^^^^^^^1^" + sb_columns1.toString());
            sb_columns2.deleteCharAt(sb_columns2.length()-1);//移除最后一个逗号字符
            sb_columns1.deleteCharAt(sb_columns1.length()-1);
//            Log.v(tag, "^^^^^^^^^^^^^^^^2^" + sb_columns1.toString());
            sb_columns1.append(" FROM ( SELECT [Node].NodeNo , [Node].Location ,[Node].Longitude,[Node].[Latitude],[ResultData2].DATETIME , CAST([ResultData2].[SensorNo] AS VARCHAR) + '_' + CAST([ResultData2].[Channel] AS VARCHAR) AS [SensorList] , [ResultData2].DATA,ResultData2.ishavevideo  FROM (");
            if (sb_columns1.length() > 0){
                sb_sql.append(sb_columns1.toString().substring(1));//删除了开头一个字符串？？
            }
//            Log.v(tag, "^^^^^^^^^^^^^^^^3^" + sb_columns1.toString().substring(6000));
            sb_sql.append(" SELECT SUBSTRING(CONVERT(VARCHAR(20), [CollectionTime], 20), 1, 19) AS DATETIME , [ResultData1].[UniqueID] , [Channel] , [SensorNo] , MAX([Data]) AS DATA,(case when vlc.URL IS     null then 0 else 1 end) as ishavevideo FROM ( SELECT TOP 99999999 * FROM [LatestData]  " + strwhere + "  ORDER BY [LatestDataID] DESC ) [ResultData1]"
                    + " left join( select vlc.URL,nod.UniqueID from Node as nod  inner join  VLCVideoInfo as vlc on nod.NodeNo=vlc.Remark " + strwhere + ")   as vlc on [ResultData1].UniqueID=vlc.UniqueID WHERE 1 = 1 GROUP BY SUBSTRING(CONVERT(VARCHAR(20), [CollectionTime] , 20) , 1 , 19), [ResultData1].[UniqueID] , [Channel] , [SensorNo],vlc.URL ) [ResultData2] JOIN [Node] ON [ResultData2].UniqueID = [Node].UniqueID JOIN [Sensor] ON [Sensor].SensorNo = [ResultData2].SensorNo ) [ResultData3] PIVOT ( MAX([ResultData3].DATA) FOR [ResultData3].[SensorList] IN ( " +
                    sb_columns2.toString()+" ) ) [SensorColumn]");
            if(orderby.contains("更新时间")){
                sb_sql.insert(0, "SELECT TOP 99999999 * , ROW_NUMBER() OVER ( ORDER BY  " + orderby + 
                        "  ) AS [RANK] from ( SELECT TOP 99999999 * , ROW_NUMBER() OVER ( PARTITION BY 节点编号 ORDER BY " + orderby + " ) AS [RANK2] FROM (");
//                sb_sql.insert(0, "SELECT TOP 99999999 * , ROW_NUMBER() OVER ( ORDER BY "+orderby+" ) AS [RANK] FROM (");
            } else {
//                sb_sql.insert(0, "SELECT TOP 99999999 * , ROW_NUMBER() OVER ( ORDER BY 更新时间 ) AS [RANK] FROM (");
                sb_sql.insert(0, "SELECT TOP 99999999 * , ROW_NUMBER() OVER ( ORDER BY 更新时间 desc ) AS [RANK] " +
                		"FROM ( SELECT TOP 99999999 * , ROW_NUMBER() OVER ( PARTITION BY 节点编号 ORDER BY 更新时间 desc) AS [RANK2] FROM (");
            }
//            sb_sql.append(" ) AS [Table] WHERE 1=1");
            sb_sql.append(" ) AS [Table] WHERE 1=1 ) as t where t.[Rank2]=1");
            if (orderby != null){
                sb_sql.append(" ORDER BY "+orderby+" ");
            }
            if (beginindex != -1 && endindex != -1){
                sb_sql.insert(0, "SELECT * FROM (");
                sb_sql.append(" ) AS [Table3] WHERE 1=1");
                sb_sql.append(" AND [Table3].[RANK] BETWEEN "+beginindex+" AND "+endindex+" ");
            }
            rs = DbHelperSQL.getInstance().Query(sb_sql.toString());
        }
        return rs;
    }
    
    public List<LatestDataModel> DataTableToList(ResultSet c){
        List<LatestDataModel> mModels = new ArrayList<LatestDataModel>();
        LatestDataModel mm = new LatestDataModel();
        try {
            while(c.next()){
                mm = DataRowToModel(c);
                if (mm != null){
                    mModels.add(mm);
                }
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mModels;
    }
    
    public LatestDataModel DataRowToModel(ResultSet row){
        LatestDataModel lm = new LatestDataModel();
        try {
            lm._latestdataid = row.getInt("LatestDataID");
            lm._collectiontime = row.getTimestamp("CollectionTime");
            lm._uniqueid = row.getString("UniqueID");
            lm._channel = row.getInt("Channel");
            lm._sensorno = row.getInt("SensorNo");
            lm._data = row.getFloat("Data");//TODO 其他好多地方这个没用到20160401
            lm._islive = row.getInt("ISLive");
            return lm;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private SsiotResult GetList(String strWhere) {
        StringBuilder strSql=new StringBuilder();
        strSql.append("select LatestDataID,CollectionTime,UniqueID,Channel,SensorNo,Data,IsLive ");
        strSql.append(" FROM LatestData ");
        if(strWhere.trim() != "") {
            strSql.append(" where "+strWhere);
        }
        return DbHelperSQL.getInstance().Query(strSql.toString());
    }
}
