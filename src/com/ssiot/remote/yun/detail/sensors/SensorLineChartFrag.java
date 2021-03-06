
package com.ssiot.remote.yun.detail.sensors;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ssiot.fish.R;
import com.ssiot.remote.data.DataAPI;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.YunNodeModel;
import com.ssiot.remote.yun.unit.XYStringHolder;
import com.ssiot.remote.yun.webapi.WS_API;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

//FarmDetailSensorLineChartFragment
public class SensorLineChartFrag extends Fragment {//原来的chart应该可以支持更多功能界面美化
    private static final String tag = "SensorLineChartFrag";
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();//创建数据层
    private XYMultipleSeriesRenderer mMulRenderer = new XYMultipleSeriesRenderer();//创建你需要的图表最下面的图层
    private XYSeries mCurrentSeries;//创建具体的数据层 
    private XYSeriesRenderer mCurrentRenderer;//创建你需要在图层上显示的具体内容的图层 
    GraphicalView mChartView;
    Button button2;
    List<XYStringHolder> mDatas = new ArrayList<XYStringHolder>();
    DeviceBean mDeviceBean;
    YunNodeModel mYunModel;
    public static final String TABLETEN = "data_DataByTen";
    public static final String TABLEHOUR = "data_DataByHour";
    public static final String TABLEDAY = "data_DataByDay";
    public static final String TABLEMONTH = "data_DataByMonth";
    public static final String TABLEYEAR = "data_DataByYear";
    public static final String[] tableList = {TABLETEN, TABLEHOUR, TABLEDAY, TABLEMONTH, TABLEYEAR};
    private String tableName = TABLEHOUR;
    
    private static final int MSG_GET_END = 1;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_GET_END:
                    refreshChart();
                    break;

                default:
                    break;
            }
        };
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDeviceBean = (DeviceBean) getArguments().getSerializable("devicebean");
        mYunModel = (YunNodeModel) getArguments().getSerializable("yunnodemodel");
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(tag, "~~~~~~~~~onCreateView~~~~~~~~~~~");
        View view = inflater.inflate(R.layout.activity_farm_monitor_detail_sensor_logchart_fragment,
                container, false);
        button2 = (Button) view.findViewById(android.R.id.button2);
        button2.setText(mDeviceBean.getUnit());
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.v(tag, "~~~~~~~~~onViewCreated~~~~~~~~~~~");
        super.onViewCreated(view, savedInstanceState);
        initChartView(view);
        new GetDataThread().start();
    }
    
    private class GetDataThread extends Thread{
        @Override
        public void run() {
//            List<NodeView2Model> nList = new AjaxGetNodesDataByUserkey().GetNodesDetailData(mYunModel.mNodeUnique, ""+nodeno, grainSize,startTime,endTime);
            String column = mDeviceBean.mName;// + (mDeviceBean.mChannel == 0 ? "" : mDeviceBean.mChannel);//TODO
//            List<XYStringHolder> list = DataAPI.getSingleSensorData(tableName, mYunModel.mNodeUnique, "Avg", column);
            int endTime = (int) (System.currentTimeMillis()/1000);
            int startTime = endTime - 30 * 3600;//databyday 的30条记录
            List<XYStringHolder> list = new WS_API().GetSensorHisData(mYunModel.mNodeUnique, startTime, endTime, 2, mDeviceBean.mDeviceTypeNo, mDeviceBean.mChannel);
            if (null != list){
                mDatas.clear();
                mDatas.addAll(list);
                Collections.reverse(mDatas);
                mHandler.sendEmptyMessage(MSG_GET_END);
            }
        }
    }
    
    @Override
    public void onDestroyView() {//viewpager滑动回来时不见了 TODO in a better way
        mChartView = null;
        mDataset.clear();
        mMulRenderer.removeAllRenderers();
        super.onDestroyView();
    }
    
    private void initChartView(View rootView) {
        if (null == mChartView) {
            LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.CurrentChartView);
            // 有时候项目中开发,需要在界面的某一块展示视图,这时候我们可以通过getLineChartView得到一个GraphicalView类型的视图
            // (这边就不要需要在AndroidManifest.xml加上<activity
            // android:name="org.achartengine.GraphicalActivity" />.)
            mMulRenderer.setPointSize(8f);// achartengine的bug 这个要在chartview生成之前设置！！ 最好是大多设置都设好
            mChartView = ChartFactory.getLineChartView(getActivity(), mDataset, mMulRenderer);
            // enable the chart click events
            mMulRenderer.setClickEnabled(true);// 是否可点击
            mMulRenderer.setSelectableBuffer(20);// 点击区域大小  设置点的缓冲半径值(在某点附近点击时,多大范围内都算点击这个点)
            mChartView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
                    if (seriesSelection == null) {
                        Log.e(tag, "No chart element");
                    } else {
//                        Toast.makeText(getActivity(),
//                                "第几条线 " + seriesSelection.getSeriesIndex()
//                                        + " 第几个点 " + seriesSelection.getPointIndex()
//                                        + "  X=" + seriesSelection.getXValue()
//                                        + ", Y="
//                                        + seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
            initChartData();
        } else {
            Log.w(tag, "----initChartView--already have chartview");
            mChartView.setBackgroundColor(Color.RED);
            mChartView.invalidate();
        }
        mChartView.repaint();
    }
    
    private void initChartData(){
        Log.v(tag, "----initChartData-----");
        float textSize = getResources().getDimension(R.dimen.subFarmDetailTextSize);
        mMulRenderer.setApplyBackgroundColor(true);// 设置是否显示背景颜色 
        mMulRenderer.setBackgroundColor(Color.TRANSPARENT);// 设置背景颜色  
        mMulRenderer.setMarginsColor(Color.TRANSPARENT);//设置周边背景色
        mMulRenderer.setAxisTitleTextSize(15);//Axis代表轴
        mMulRenderer.setChartTitleTextSize(0);//设置图表标题字体大小,可以设置0是把标题隐藏掉
        mMulRenderer.setLabelsTextSize(textSize);//15
        mMulRenderer.setLegendTextSize(textSize);
        mMulRenderer.setMargins(new int[] { 30, 55, 45 ,10});//20, 30, 15, 0 
        mMulRenderer.setZoomButtonsVisible(false);//一个大bug？？？？会drawbitmap空指针
        mMulRenderer.setPointSize(10.0f); // 设置图表上显示点的大小 ??
        mMulRenderer.setLabelsColor(Color.RED);//// 设置坐标颜色
        mMulRenderer.setAxesColor(getResources().getColor(R.color.DarkGreen));//设置坐标轴颜色？？
        mMulRenderer.setXLabels(10);//copy huiyun ??//设置X轴显示的刻度标签的个数
        mMulRenderer.setYLabels(10);
        mMulRenderer.setXLabelsColor(getResources().getColor(R.color.c333333));
        mMulRenderer.setYLabelsColor(0, getResources().getColor(R.color.c333333));
        mMulRenderer.setXLabelsAlign(Paint.Align.CENTER);//设置刻度线与X轴之间的相对位置
        mMulRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        mMulRenderer.setShowLegend(false);//设置是否显示图例.//是否显示图例说明
        mMulRenderer.setShowGrid(true);//设置是否显示网格
        mMulRenderer.setGridColor(getResources().getColor(R.color.help_button_view));
        mMulRenderer.setChartTitleTextSize(1f+textSize);
        mMulRenderer.setAxisTitleTextSize(textSize);
        mMulRenderer.setPanEnabled(true, false);//设置是否允许拖动
        mMulRenderer.setPointSize(400.0f);
        mMulRenderer.setFitLegend(true);//整合适的位置
//        mMulRenderer.setXLabelsAngle(-60f);//设置x轴显示的倾斜度
        
        XYSeries series = new XYSeries("类似于sheet1");//创建具体的数据层 
        mDataset.addSeries(series);
        mCurrentSeries = series;
        // create a new renderer for the new series
        XYSeriesRenderer renderer = new XYSeriesRenderer();//创建你需要在图层上显示的具体内容的图层 
        mMulRenderer.addSeriesRenderer(renderer);
        // set some renderer properties
        renderer.setPointStyle(PointStyle.CIRCLE);///折线点的样式
//        renderer.setPointStrokeWidth(30f);//折线点的大小 ???错
        renderer.setColor(getResources().getColor(R.color.blue_2));//R.color.sta_line
        renderer.setFillPoints(true);
        renderer.setLineWidth(3.0f);//折线宽度
        renderer.setDisplayChartValues(true);//设置显示折线的点对应的值
        renderer.setDisplayChartValuesDistance(5);//折线点的值距离折线点的距离
        renderer.setChartValuesSpacing(20.0f);
        renderer.setChartValuesTextSize(textSize);
        
        mChartView.repaint();
        mCurrentRenderer = renderer;
//        mChartView.repaint();
//        for (int i = 0; i < 17; i ++){//this is test
//            Random ra = new Random();
//            mCurrentSeries.add(i+1, ra.nextInt(10));//添加数据,一般都是for循环数据不断操作这一步添加的
//            mMulRenderer.addTextLabel(i+1, "x轴"+(i+1));
//            //x轴不显示原先的数字
//        }
        mMulRenderer.setXLabels(0);//设置X轴显示的刻度标签的个数 如果想要在X轴显示自定义的标签，那么首先要设置renderer.setXLabels(0);  如果不设置为0，那么所设置的Labels会与原X坐标轴labels重叠
        mChartView.repaint();
        Log.v(tag, "----initChartData----- end");
    }
    
    private void refreshChart(){
        if (null != mChartView){
            mCurrentSeries.clear();
            if (null == mDatas || mDatas.size() == 0){
                Toast.makeText(getActivity(), "曲线图-近期无数据", Toast.LENGTH_SHORT).show();
                return;
            }
            int size = mDatas.size();
            for (int i = 0; i < mDatas.size(); i ++){
                mCurrentSeries.add(i + 1, mDatas.get(size - i -1).yData); 
                mMulRenderer.addTextLabel(i+1, formatXString(mDatas.get(size - i - 1).xString));
            }
            DecimalFormat df1 = new DecimalFormat(getLongestNumFormat(mDatas));
            mCurrentRenderer.setChartValuesFormat(df1);//值的显示小数点后位置
            double max = findMax();
            double min = findMin();
            mMulRenderer.setYAxisMax(max + (max - min)/10);
            mMulRenderer.setYAxisMin(min - (max-min)/20);
            mMulRenderer.setXAxisMin(0);
            mMulRenderer.setXAxisMax(7);
            mChartView.repaint();
        }
    }
    
    private double findMax(){
        double max = 0;
        if (null != mDatas && mDatas.size() > 0){
            max = mDatas.get(0).yData;
            for (XYStringHolder h : mDatas){
                if (max < h.yData){
                    max = h.yData;
                }
            }
        }
        return max;
    }
    
    private double findMin(){
        double min = 0;
        if (null != mDatas && mDatas.size() > 0){
            min = mDatas.get(0).yData;
            for (XYStringHolder h : mDatas){
                if (min > h.yData){
                    min = h.yData;
                }
            }
        }
        return min;
    }
    
    private String getLongestNumFormat(List<XYStringHolder> list){
        int longest = 0;
        if (null != list){
            for (XYStringHolder t : list){
                String str = ""+t.yData;
                int tmp = str.length() - str.indexOf(".") - 1;
                if (longest < tmp){
                    longest = tmp;
                }
            }
        }
        if (longest > 0){
            String formatStr = "0.";
            for (int i = 0; i < longest; i ++){
                formatStr += "0";
            }
            Log.v(tag, "----------getLongestNumFormat--------" + formatStr);
            return formatStr;
        }
        return "";
    }
    
    private String formatXString(String str){
        Timestamp t = Timestamp.valueOf(str);
        if (tableName.equals(TABLETEN)){
            return ""+t.getHours() + ":" +t.getMinutes();
        } else if (tableName.equals(TABLEHOUR)){
            return t.getDate() + "日"+t.getHours() + "时";
        } else if (tableName.equals(TABLEDAY)){
            return ""+t.getDate() +"日";
        } else if (tableName.equals(TABLEMONTH)){
            return ""+t.getMonth() + "月";
        } else if (tableName.equals(TABLEYEAR)){
            return "" + (t.getYear() + 1900) + "年";
        }
        return str;
    }
    
    public boolean canScroll(int width, int height){//解决嵌套滑动的问题
        Rect rect = new Rect();
        mChartView.getLocalVisibleRect(rect);
        return rect.contains(width, height);
    }
}
