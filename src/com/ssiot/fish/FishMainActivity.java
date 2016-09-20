package com.ssiot.fish;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.AdapterView;
import android.widget.Toast;

import com.ssiot.fish.facility.FishpondMainActivity;
import com.ssiot.fish.question.QuestionListActivity;
import com.ssiot.fish.task.TaskActivity;
import com.ssiot.remote.BrowserActivity;
import com.ssiot.remote.LoginActivity;
import com.ssiot.remote.MainActivity;
import com.ssiot.remote.SettingFrag;
import com.ssiot.remote.SsiotService;
import com.ssiot.remote.UpdateManager;
import com.ssiot.remote.Utils;
import com.ssiot.remote.aliyun.MsgListAct;
import com.ssiot.remote.aliyun.TxtAct;
import com.ssiot.remote.data.business.IOTCompany;
import com.ssiot.remote.expert.DiagnoseFishSelectActivity;
import com.ssiot.remote.expert.FacilityNodeListAct;
import com.ssiot.remote.expert.WaterAnalysisLauncherAct;
import com.ssiot.remote.expert.WaterColorDiagnoseAct;
import com.ssiot.remote.yun.monitor.MonitorAct;
import com.ssiot.remote.yun.sta.StatisticsAct;

import java.util.ArrayList;
import java.util.HashMap;

public class FishMainActivity extends HeadActivity{
    private static final String tag = "FishMainActivity";
    private GridView gridView1;
    ArrayList<CellModel> cells;
    private SharedPreferences mPref;
    
    private UpdateManager mUpdaManager;
    private Notification mNoti;
    private Context mContext;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main_fish);
        startService(new Intent(this, SsiotService.class));
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        gridView1 = (GridView) findViewById(R.id.gridView1);
        initGridView();
        
        if (mPref.getBoolean(Utils.PREF_AUTOUPDATE, true) == true){
            mUpdaManager = new UpdateManager(this);
            mUpdaManager.startGetRemoteVer();
        }
    }
    
    private void initGridView(){
        cells = new ArrayList<CellModel>();
        cells.add(new CellModel(R.drawable.cell_cekong,"监测预警", "map_zhengwu"));//测控中心
//        cells.add(new CellModel(R.drawable.cell_data,"控制中心", "map_qiye"));//统计报表
//        cells.add(new CellModel(R.drawable.cell_video,"视频监控", "map_jstg"));//
        cells.add(new CellModel(R.drawable.cell_data,"数据中心", "xxxxx"));
//        cells.add(new CellModel(R.drawable.cell_renwuzhongxin,"任务中心", "map_zhuisu"));//
        cells.add(new CellModel(R.drawable.cell_yubingzhengduan,"鱼病诊断", "map_xinxi"));//
        cells.add(new CellModel(R.drawable.cell_yubingzhengduan,"水色诊断", "map_shuise"));
        cells.add(new CellModel(R.drawable.cell_zhuanjiazaixian,"专家在线", "map_jinrong",CellModel.MODE_URL).setUrl("http://www.zhdcoop.com/"));
        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"生产管理", "map_webbrow"));//
//        cells.add(new CellModel(R.drawable.cell_yuchangguanli,"渔场管理", "map_qiyezaixian"));//
        cells.add(new CellModel(R.drawable.cell_wuzijiaoyi,"物资交易", "map_yztb"));//
        cells.add(new CellModel(R.drawable.cell_hudongjiaoliu,"互动交流", "map_hudong"));
        cells.add(new CellModel(R.drawable.cell_qiyehuizong,"企业汇总", "bdsj"));//
        cells.add(new CellModel(R.drawable.cell_shichangdongtai,"市场动态", "map_yonghuzhinan"));//
        cells.add(new CellModel(R.drawable.cell_shichangdongtai,"消息记录", "map_xiaoxi"));//
        cells.add(new CellModel(R.drawable.cell_shichangdongtai,"水质分析", "map_shuizhifenxi"));//
        cells.add(new CellModel(R.drawable.cell_shichangdongtai,"气象信息", "map_shuizhifenxi"));//
        cells.add(new CellModel(R.drawable.cell_shichangdongtai,"技术资讯", "map_shuizhifenxi"));
//        cells.add(new CellModel(R.drawable.cell_zhengwuguanli,"更新", "map_findupdate"));
        ImageAdapter adapter = new ImageAdapter(this, cells);
        gridView1.setAdapter(adapter);
        gridView1.setSelector(new ColorDrawable(0));
        gridView1.setOnItemClickListener(gvAppListen);
    }
    
    AdapterView.OnItemClickListener gvAppListen = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (null != cells){
                CellModel model = cells.get(position);
//                switch (model.openType) {
//                    case CellModel.MODE_URL:
//                        Intent intent1 = new Intent(FishMainActivity.this, BrowserActivity.class);
//                        intent1.putExtra("url", model.urlString);
//                        startActivity(intent1);
//                        break;
//
//                    default:
//                        break;
//                }
                if ("监测预警".equals(model.itemText)){
//                    Intent intent = new Intent(FishMainActivity.this, MoniAndCtrlActivity.class);
//                    intent.putExtra("defaulttab", 1);
                    Intent intent = new Intent(mContext, MonitorAct.class);
                    startActivity(intent);
                } else if ("控制中心".equals(model.itemText)){
                    Intent intent = new Intent(FishMainActivity.this, MoniAndCtrlActivity.class);
                    intent.putExtra("defaulttab", 2);
                    startActivity(intent);
                } else if ("视频监控".equals(model.itemText)){
                    Intent intent = new Intent(FishMainActivity.this, VideoListActivity.class);
                    startActivity(intent);
                } else if ("数据中心".equals(model.itemText)){
                	Intent intent = new Intent(mContext, StatisticsAct.class);
                	startActivity(intent);
                } else if ("互动交流".equals(model.itemText)){
                    Intent intent = new Intent(FishMainActivity.this, QuestionListActivity.class);
                    intent.putExtra("isexpertmode", false);
                    startActivity(intent);
                } else if ("鱼病诊断".equals(model.itemText)){
                    Intent intent = new Intent(FishMainActivity.this, DiagnoseFishSelectActivity.class);
                    startActivity(intent);
                } else if ("水色诊断".equals(model.itemText)){
                	Intent intent = new Intent(FishMainActivity.this, WaterColorDiagnoseAct.class);
                	startActivity(intent);
                } else if ("渔场管理".equals(model.itemText)){
                    Intent intent = new Intent(FishMainActivity.this, FishpondMainActivity.class);
                    startActivity(intent);
                } else if ("专家在线".equals(model.itemText)){//专家在线只是问题类别不同
                    Intent intent = new Intent(FishMainActivity.this, QuestionListActivity.class);
                    intent.putExtra("isexpertmode", true);
                    startActivity(intent);
                } else if ("生产管理".equals(model.itemText)){//TODO 三代界面之后是这个生产管理界面
                    Intent intent = new Intent(FishMainActivity.this, com.ssiot.remote.yun.manage.ProductManageActivity.class);
                    startActivity(intent);
                } else if ("任务中心".equals(model.itemText)){
                    Intent intent = new Intent(FishMainActivity.this, TaskActivity.class);
                    startActivity(intent);
                } else if ("物资交易".equals(model.itemText)){
                    Intent intent = new Intent(FishMainActivity.this, BrowserActivity.class);
                    intent.putExtra("url", "http://gn.ssiot.com/mobile2/index.html");
                    startActivity(intent);
                } else if ("企业汇总".equals(model.itemText)){
                    Intent intent = new Intent(FishMainActivity.this, CompanyListActivity.class);
                    startActivity(intent);
                } else if ("市场动态".equals(model.itemText)){
                    Intent intent = new Intent(FishMainActivity.this, MarketNewsActivity.class);
                    startActivity(intent);
                } else if ("消息记录".equals(model.itemText)){
                    Intent intent = new Intent(FishMainActivity.this, MsgListAct.class);//MsgListAct
                    startActivity(intent);
                } else if ("水质分析".equals(model.itemText)){
                    Intent intent = new Intent(FishMainActivity.this, WaterAnalysisLauncherAct.class);
                    startActivity(intent);
                } else if ("气象信息".equals(model.itemText)){
                    Intent intent = new Intent(FishMainActivity.this, WeatherLaunchAct.class);
                    startActivity(intent);
                } else if ("技术资讯".equals(model.itemText)){
                    Intent intent = new Intent(FishMainActivity.this, ArticleListAct.class);
                    startActivity(intent);
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        getMenuInflater().inflate(R.menu.menu_f_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                Intent loginIntent = new Intent(FishMainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                SsiotService.cancel = true;
                stopService(new Intent(this, SsiotService.class));
                finish();
                Editor e = mPref.edit();
                e.putString(Utils.PREF_PWD, "");
                e.commit();
                return true;
            case R.id.action_frag_main_setting:
                Intent intent = new Intent(FishMainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void showUpdateChoseDialog(HashMap<String, String> mVerMap){
        final HashMap<String, String> tmpMap = mVerMap;
        AlertDialog.Builder builder =new Builder(this);
        builder.setTitle(R.string.soft_update_title);
        builder.setMessage(R.string.soft_update_info);
        builder.setPositiveButton(R.string.soft_update_updatebtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mNoti = mUpdaManager.showNotification(FishMainActivity.this);
//                        .setProgressBar(R.id.noti_progress, 100, 0, false);
                mUpdaManager.startDownLoad(tmpMap);
//                showDownloadDialog(tmpMap);
                dialog.dismiss();
                Editor e = mPref.edit();
                e.putBoolean(Utils.PREF_AUTOUPDATE, true);
                e.commit();
                Toast.makeText(FishMainActivity.this, "转向后台下载，可在通知栏中查看进度。", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.soft_update_later, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Editor e = mPref.edit();
                e.putBoolean(Utils.PREF_AUTOUPDATE, false);
                e.commit();
                dialog.dismiss();
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }
    
//    @Override
//    public void onFSettingBtnClick() {
//        if (mUpdaManager == null){
//            mUpdaManager = new UpdateManager(FishMainActivity.this, mHandler);
//        }
//        if (mPref.getBoolean(Utils.PREF_AUTOUPDATE, true) == false){
//            Editor editor = mPref.edit();
//            editor.putBoolean(Utils.PREF_AUTOUPDATE, true);
//            editor.commit();
//        }
//        mUpdaManager.startGetRemoteVer();
//    }
}
